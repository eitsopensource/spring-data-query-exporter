package br.com.eits.queryexport.exporter;

import java.beans.Introspector;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import br.com.eits.queryexport.EntityPropertyExtractor;
import br.com.eits.queryexport.QueryColumn;
import br.com.eits.queryexport.exporter.formatters.ExportFormatters;
import com.opencsv.CSVWriter;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

public class CSVExporter implements Exporter
{
	private final MessageSource messageSource;

	public CSVExporter( MessageSource messageSource )
	{
		this.messageSource = messageSource;
	}

	@Override
	public ByteArrayOutputStream export( Class<?> entityClass, String queryName, List<QueryColumn> columns, List<?> rows )
	{
		List<String[]> lines = new ArrayList<>();
		lines.add( columns.stream().map( QueryColumn::getLocalizedLabel ).toArray( String[]::new ) );
		lines.addAll(
				rows.stream().map( entity ->
						columns.stream().map( column -> Pair.of( column, EntityPropertyExtractor.extractPropertyValue( entity, column.getAttributePath() ) ) )
								.map( pair -> convertValue( entityClass, pair ) )
								.toArray( String[]::new ) )
						.collect( Collectors.toList() ) );

		try (
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				OutputStreamWriter streamWriter = new OutputStreamWriter( outputStream );
				CSVWriter writer = new CSVWriter( streamWriter ) )
		{
			writer.writeAll( lines );
			return outputStream;
		}
		catch ( IOException e )
		{
			throw new IllegalStateException( e );
		}
	}

	private String convertValue( Class<?> entityClass, Pair<QueryColumn, Object> pair )
	{
		val typeName = Introspector.decapitalize( entityClass.getSimpleName() );
		QueryColumn column = pair.getLeft();
		Object value = pair.getRight();
		if ( value != null )
		{
			if ( value instanceof Enum<?> )
			{
				return messageSource.getMessage( typeName + "." + column.getAttributePath() + "." + ((Enum<?>) value).name(), null, LocaleContextHolder.getLocale() );
			}
			else if ( value instanceof Boolean )
			{
				return messageSource.getMessage( typeName + "." + column.getAttributePath() + "." + value, null, LocaleContextHolder.getLocale() );
			}
		}
		return ExportFormatters.convertIfNecessary( column, value );
	}
}
