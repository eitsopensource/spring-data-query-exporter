package br.com.eits.queryexport.exporter.formatters;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import ar.com.fdvs.dj.domain.CustomExpression;

public class OffsetDateTimeFormatter implements CustomExpression, ObjectFormatter<OffsetDateTime>
{
	private final String property;
	private final DateTimeFormatter formatter;

	public OffsetDateTimeFormatter( String property, String pattern )
	{
		this.property = property;
		this.formatter = DateTimeFormatter.ofPattern( pattern );
	}

	@Override
	public Object evaluate( Map fields, Map variables, Map parameters )
	{
		OffsetDateTime time = (OffsetDateTime) fields.get( property );
		return time.format( formatter );
	}

	@Override
	public String getClassName()
	{
		return String.class.getName();
	}

	@Override
	public String format( OffsetDateTime object, String pattern )
	{
		return DateTimeFormatter.ofPattern( pattern ).format( object );
	}

	@Override
	public CustomExpression getCustomExpression( String propertyPath )
	{
		return null;
	}
}
