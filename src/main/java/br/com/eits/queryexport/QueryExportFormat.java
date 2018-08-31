package br.com.eits.queryexport;

import br.com.eits.queryexport.exporter.CSVExporter;
import br.com.eits.queryexport.exporter.Exporter;
import br.com.eits.queryexport.exporter.PDFExporter;
import br.com.eits.queryexport.exporter.XLSExporter;
import lombok.Getter;
import org.directwebremoting.annotations.DataTransferObject;

@Getter
@DataTransferObject(type = "enum")
public enum QueryExportFormat
{
	PDF( PDFExporter.class, "application/pdf", "pdf" ),
	XLS( XLSExporter.class, "application/octet-stream", "xls" ),
	CSV( CSVExporter.class, "application/octet-stream", "csv" );

	private final Class<? extends Exporter> exporter;
	private final String mimeType;
	private final String extension;

	QueryExportFormat( Class<? extends Exporter> exporter, String mimeType, String extension )
	{
		this.exporter = exporter;
		this.mimeType = mimeType;
		this.extension = extension;
	}
}
