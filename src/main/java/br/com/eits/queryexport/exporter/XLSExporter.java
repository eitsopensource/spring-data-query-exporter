package br.com.eits.queryexport.exporter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import br.com.eits.queryexport.EntityPropertyExtractor;
import br.com.eits.queryexport.QueryColumn;
import br.com.eits.queryexport.exporter.formatters.ExportFormatters;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class XLSExporter implements Exporter
{
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
				String columnText = ExportFormatters.convertIfNecessary( columns.get( j ), EntityPropertyExtractor.extractPropertyValue( rows.get( i ), columns.get( j ).getAttributePath() ) );
				row.createCell( j ).setCellValue( columnText );
			}
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
