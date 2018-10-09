package br.com.eits.queryexport.exporter;

import java.awt.image.BufferedImage;
import java.beans.Introspector;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.ColumnProperty;
import ar.com.fdvs.dj.domain.CustomExpression;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.ImageBanner;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.FastReportBuilder;
import ar.com.fdvs.dj.domain.constants.Border;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.constants.HorizontalAlign;
import ar.com.fdvs.dj.domain.constants.Page;
import br.com.eits.queryexport.EntityPropertyExtractor;
import br.com.eits.queryexport.QueryColumn;
import br.com.eits.queryexport.QueryExportConfiguration;
import br.com.eits.queryexport.exporter.formatters.ExportFormatters;
import br.com.eits.queryexport.exporter.formatters.LocalDateTimeFormatter;
import br.com.eits.queryexport.exporter.formatters.OffsetDateTimeFormatter;
import lombok.val;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.commons.io.IOUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

public class PDFExporter implements Exporter
{
	private static final String FONT_NAME = Font._FONT_ARIAL;
	private static final int BANNER_HEIGHT = 48;

	private final String reportTitle;
	private final BufferedImage reportImage;
	private final File reportImagePath;
	private final MessageSource messageSource;

	public PDFExporter( QueryExportConfiguration queryExportConfiguration, MessageSource messageSource )
	{
		this.messageSource = messageSource;
		try
		{
			this.reportTitle = queryExportConfiguration.getReportTitle();
			if ( queryExportConfiguration.getReportImage() != null )
			{
				this.reportImage = ImageIO.read( getClass().getResourceAsStream( queryExportConfiguration.getReportImage() ) );
				this.reportImagePath = File.createTempFile( "singra-report", "png" );
				FileOutputStream fos = new FileOutputStream( reportImagePath );
				IOUtils.copy( getClass().getResourceAsStream( queryExportConfiguration.getReportImage() ), fos );
				fos.close();
			}
			else
			{
				this.reportImage = null;
				this.reportImagePath = null;
			}
		}
		catch ( IOException e )
		{
			throw new IllegalArgumentException( e );
		}
	}

	@Override
	public ByteArrayOutputStream export( Class<?> entityClass, String queryName, List<QueryColumn> columns, List<?> rows )
	{
		try
		{
			FastReportBuilder builder = new FastReportBuilder();
			for ( QueryColumn column : columns )
			{
				Class<?> propType = EntityPropertyExtractor.extractPropertyType( entityClass, column.getAttributePath() );
				ColumnBuilder columnBuilder = ColumnBuilder.getNew();
				columnBuilder.setTitle( column.getLocalizedLabel() );
				columnBuilder.setColumnProperty( new ColumnProperty( column.getAttributePath(), propType ) );
				columnBuilder.setWidth( column.getPercentualWidth() == 0 ? 100 / columns.size() : column.getPercentualWidth() );
				if ( Calendar.class.isAssignableFrom( propType ) || Date.class.isAssignableFrom( propType ) )
				{
					columnBuilder.setPattern( column.getDateTimePattern() );
				}
				else if ( LocalDateTime.class.isAssignableFrom( propType ) )
				{
					columnBuilder.setCustomExpression( new LocalDateTimeFormatter( column.getAttributePath(), column.getDateTimePattern() ) );
				}
				else if ( OffsetDateTime.class.isAssignableFrom( propType ) )
				{
					columnBuilder.setCustomExpression( new OffsetDateTimeFormatter( column.getAttributePath(), column.getDateTimePattern() ) );
				}
				else if ( Boolean.class.isAssignableFrom( propType ) )
				{
					columnBuilder.setCustomExpression( new CustomExpression()
					{
						@Override
						public Object evaluate( Map fields, Map variables, Map parameters )
						{
							val boolValue = (Boolean) fields.get( column.getAttributePath() );
							if ( boolValue == null )
							{
								return null;
							}
							val messageField = Introspector.decapitalize( entityClass.getSimpleName() ) + "." + column.getAttributePath() + "." + boolValue;
							return messageSource.getMessage( messageField, null, LocaleContextHolder.getLocale() );
						}

						@Override
						public String getClassName()
						{
							return String.class.getName();
						}
					} );
				}
				else if ( Enum.class.isAssignableFrom( propType ) )
				{
					columnBuilder.setCustomExpression( new CustomExpression()
					{
						@Override
						public Object evaluate( Map fields, Map variables, Map parameters )
						{
							val enumValue = (Enum<?>) fields.get( column.getAttributePath() );
							if ( enumValue == null )
							{
								return null;
							}
							val messageField = Introspector.decapitalize( entityClass.getSimpleName() ) + "." + column.getAttributePath() + "." + enumValue.name();
							return messageSource.getMessage( messageField, null, LocaleContextHolder.getLocale() );
						}

						@Override
						public String getClassName()
						{
							return String.class.getName();
						}
					} );
				}
				else if ( Number.class.isAssignableFrom( propType ) && column.isFormattingEnabled() )
				{
					val useFloatStyle = Stream.of( Double.class, Float.class, BigDecimal.class ).anyMatch( c -> c.isAssignableFrom( propType ) );
					columnBuilder.setCustomExpression( new CustomExpression()
					{
						@Override
						public Object evaluate( Map fields, Map variables, Map parameters )
						{
							val numberValue = (Number) fields.get( column.getAttributePath() );
							if ( numberValue == null )
							{
								return "";
							}
							val decimalFormat = useFloatStyle ?
									DecimalFormat.getNumberInstance( LocaleContextHolder.getLocale() ) :
									DecimalFormat.getInstance( LocaleContextHolder.getLocale() );
							return decimalFormat.format( useFloatStyle ? numberValue.doubleValue() : numberValue.longValue() );
						}

						@Override
						public String getClassName()
						{
							return String.class.getName();
						}
					} );
				}
				else if ( ExportFormatters.FORMATTERS.containsKey( propType ) )
				{
					CustomExpression expression = ExportFormatters.FORMATTERS.get( propType ).getCustomExpression( column.getAttributePath() );
					if ( expression != null )
					{
						columnBuilder.setCustomExpression( expression );
					}
				}
				builder.addColumn( columnBuilder.build() );
			}

			builder.setTitle( reportTitle );
			builder.setSubtitle( queryName );
			builder.setDefaultStyles( titleStyle(), subtitleStyle(), headerStyle(), detailStyle() );
			builder.setUseFullPageWidth( true );
			builder.setPageSizeAndOrientation( Page.Page_A4_Landscape() );
			builder.setHeaderHeight( 16 );
			if ( reportImagePath != null )
			{
				builder.addFirstPageImageBanner( reportImagePath.getAbsolutePath(), (int) getImageWidth(), BANNER_HEIGHT, ImageBanner.ALIGN_LEFT );
			}

			DynamicReport report = builder.build();
			JRDataSource dataSource = new JRBeanCollectionDataSource( rows );
			JasperPrint jasperPrint = DynamicJasperHelper.generateJasperPrint( report, new ClassicLayoutManager(), dataSource );
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			JasperExportManager.exportReportToPdfStream( jasperPrint, baos );
			return baos;
		}
		catch ( Exception e )
		{
			throw new IllegalStateException( "Erro ao exportar consulta para PDF: " + e.getMessage(), e );
		}
	}

	private Style titleStyle()
	{
		final Style style = new Style();
		style.setFont( new Font( 18, FONT_NAME, true ) );
		style.setHorizontalAlign( HorizontalAlign.CENTER );
		style.setPaddingBottom( 15 );
		return style;
	}

	private Style subtitleStyle()
	{
		final Style style = new Style();
		style.setFont( new Font( 15, FONT_NAME, true ) );
		style.setHorizontalAlign( HorizontalAlign.CENTER );
		style.setPaddingBottom( 15 );
		return style;
	}

	private Style headerStyle()
	{
		final Style style = new Style();
		style.setFont( new Font( 12, FONT_NAME, true ) );
		style.setTransparent( true );
		style.setBorder( Border.PEN_1_POINT() );
		style.setPadding( 2 );
		style.setHorizontalAlign( HorizontalAlign.CENTER );
		return style;
	}

	private Style detailStyle()
	{
		final Style style = new Style();
		style.setFont( new Font( 12, FONT_NAME, false ) );
		style.setTransparent( true );
		style.setBorder( Border.PEN_1_POINT() );
		style.setPadding( 2 );
		return style;
	}

	private double getImageWidth()
	{
		return ((double) BANNER_HEIGHT / this.reportImage.getHeight()) * this.reportImage.getWidth();
	}
}
