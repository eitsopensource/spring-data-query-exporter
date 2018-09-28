package br.com.eits.queryexport.exporter.formatters;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import ar.com.fdvs.dj.domain.CustomExpression;

public class LocalDateTimeFormatter implements CustomExpression, ObjectFormatter<LocalDateTime>
{
	private final String property;
	private final DateTimeFormatter formatter;

	public LocalDateTimeFormatter( String property, String pattern )
	{
		this.property = property;
		this.formatter = DateTimeFormatter.ofPattern( pattern );
	}

	@Override
	public Object evaluate( Map fields, Map variables, Map parameters )
	{
		LocalDateTime time = (LocalDateTime) fields.get( property );
		return time.format( formatter );
	}

	@Override
	public String getClassName()
	{
		return String.class.getName();
	}

	@Override
	public String format( LocalDateTime object, String pattern )
	{
		return DateTimeFormatter.ofPattern( pattern ).format( object );
	}

	@Override
	public CustomExpression getCustomExpression( String propertyPath )
	{
		return null;
	}
}
