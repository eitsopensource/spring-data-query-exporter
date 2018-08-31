package br.com.eits.queryexport.exporter.formatters;

public interface DateFormatter<T>
{
	String format( T object, String pattern );
}
