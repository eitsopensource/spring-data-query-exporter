package br.com.eits.queryexport.exporter;

import java.beans.Introspector;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import br.com.eits.queryexport.EntityPropertyExtractor;
import br.com.eits.queryexport.QueryColumn;
import br.com.eits.queryexport.exporter.formatters.ExportFormatters;
import lombok.val;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.hibernate.Hibernate;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

public class XLSExporter implements Exporter
{
	private final MessageSource messageSource;

	public XLSExporter( MessageSource messageSource )
	{
		this.messageSource = messageSource;
	}

	@Override
	public ByteArrayOutputStream export( Class<?> entityClass, String queryName, List<QueryColumn> columns, List<?> rows )
	{
		Workbook workbook = new HSSFWorkbook();
		Sheet sheet = workbook.createSheet();
		renderHeader( sheet, columns );
		renderBody( sheet, columns, rows );

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try
		{
			workbook.write( outputStream );
		}
		catch ( IOException e )
		{
			throw new IllegalStateException( e );
		}
		return outputStream;
	}

	private void renderBody( Sheet sheet, List<QueryColumn> columns, List<?> rows )
	{
		for ( int i = 0; i < rows.size(); i++ )
		{
			Row row = sheet.createRow( i + 1 );
			for ( int j = 0; j < columns.size(); j++ )
			{
				val cell = row.createCell( j );
				val rowType = EntityPropertyExtractor.extractPropertyType( rows.get( i ), columns.get( j ).getAttributePath() );
				val rowValue = EntityPropertyExtractor.extractPropertyValue( rows.get( i ), columns.get( j ).getAttributePath() );
				val typeName = Introspector.decapitalize( Hibernate.getClass( rows.get( i ) ).getSimpleName() );
				if ( rowType != null && rowValue != null )
				{
					if ( Number.class.isAssignableFrom( rowType ) )
					{
						val cellStyle = cell.getCellStyle();
						val useFloatStyle = rowValue instanceof Double || rowValue instanceof Float || rowValue instanceof BigDecimal;
						if ( columns.get( j ).isFormattingEnabled() )
						{
							cellStyle.setDataFormat( useFloatStyle ?
									sheet.getWorkbook().createDataFormat().getFormat( "#,##0.00" ) :
									sheet.getWorkbook().createDataFormat().getFormat( "#,##0" ) );
						}
						cell.setCellValue( useFloatStyle ? ((Number) rowValue).doubleValue() : ((Number) rowValue).longValue() );
					}
					else if ( Boolean.class.isAssignableFrom( rowType ) )
					{
						cell.setCellValue( messageSource.getMessage( typeName + "." + columns.get( j ).getAttributePath() + "." + rowValue, null, LocaleContextHolder.getLocale() ) );
					}
					else if ( rowValue instanceof Enum<?> )
					{
						cell.setCellValue( messageSource.getMessage( typeName + "." + columns.get( j ).getAttributePath() + "." + ((Enum<?>) rowValue).name(), null, LocaleContextHolder.getLocale() ) );
					}
					else
					{
						String columnText = ExportFormatters.convertIfNecessary( columns.get( j ), EntityPropertyExtractor.extractPropertyValue( rows.get( i ), columns.get( j ).getAttributePath() ) );
						cell.setCellValue( columnText );
					}
				}

			}
		}
		for ( int i = 0; i < columns.size(); i++ )
		{
			sheet.autoSizeColumn( i );
		}
	}

	private void renderHeader( Sheet sheet, List<QueryColumn> columns )
	{
		Row headerRow = sheet.createRow( 0 );
		for ( int i = 0; i < columns.size(); i++ )
		{
			headerRow.createCell( i ).setCellValue( columns.get( i ).getLocalizedLabel() );
		}
	}
}
