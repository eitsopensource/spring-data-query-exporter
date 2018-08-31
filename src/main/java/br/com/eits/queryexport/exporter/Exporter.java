package br.com.eits.queryexport.exporter;

import java.io.ByteArrayOutputStream;
import java.util.List;

import br.com.eits.queryexport.QueryColumn;

public interface Exporter
{
	ByteArrayOutputStream export( Class<?> entityClass, String queryName, List<QueryColumn> columns, List<?> rows );
}
