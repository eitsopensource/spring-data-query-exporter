package br.com.eits.queryexport;

import java.io.ByteArrayOutputStream;

import lombok.Getter;
import org.directwebremoting.io.FileTransfer;

@Getter
public class ExportedWrapper
{
	private final QueryExportFormat format;
	private final ByteArrayOutputStream outputStream;
	private final String queryName;

	ExportedWrapper( QueryExportFormat format, ByteArrayOutputStream outputStream, String queryName )
	{
		this.format = format;
		this.outputStream = outputStream;
		this.queryName = queryName;
	}

	public FileTransfer fileTransfer()
	{
		return new FileTransfer( String.format( "%s.%s", queryName, format.getExtension() ),
				format.getMimeType(), outputStream.toByteArray() );
	}
}
