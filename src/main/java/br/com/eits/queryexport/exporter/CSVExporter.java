package br.com.eits.queryexport.exporter;

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
import org.apache.commons.lang3.tuple.Pair;

public class CSVExporter implements Exporter
{
	@Override
	public ByteArrayOutputStream export( Class<?> entityClass, String queryName, List<QueryColumn> columns, List<?> rows )
	{
		List<String[]> lines = new ArrayList<>();
		lines.add( columns.stream().map( QueryColumn::getLocalizedLabel ).toArray( String[]::new ) );
		lines.addAll(
				rows.stream().map( entity ->
						columns.stream().map( column -> Pair.of( column, EntityPropertyExtractor.extractPropertyValue( entity, column.getAttributePath() ) ) )
								.map( this::convertValue )
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

	private String convertValue( Pair<QueryColumn, Object> pair )
	{
		QueryColumn column = pair.getLeft();
		Object value = pair.getRight();
		return ExportFormatters.convertIfNecessary( column, value );
	}
}
