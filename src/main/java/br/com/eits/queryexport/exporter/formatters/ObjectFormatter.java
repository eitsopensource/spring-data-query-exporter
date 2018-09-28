package br.com.eits.queryexport.exporter.formatters;

import ar.com.fdvs.dj.domain.CustomExpression;

public interface ObjectFormatter<T>
{
	String format( T object, String pattern );

	CustomExpression getCustomExpression( String propertyPath );
}
