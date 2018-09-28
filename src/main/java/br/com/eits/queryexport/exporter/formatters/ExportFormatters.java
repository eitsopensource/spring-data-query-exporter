package br.com.eits.queryexport.exporter.formatters;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import br.com.eits.queryexport.QueryColumn;

@SuppressWarnings("unchecked")
public abstract class ExportFormatters
{
	public static final Map<Class<?>, ObjectFormatter<?>> FORMATTERS = new HashMap<>();

	static
	{
		FORMATTERS.put( Calendar.class, new CalendarFormatter() );
		FORMATTERS.put( LocalDateTime.class, new LocalDateTimeFormatter( "", "" ) );
		FORMATTERS.put( OffsetDateTime.class, new OffsetDateTimeFormatter( "", "" ) );
	}

	public static boolean needsConversionToString( Object value )
	{
		return FORMATTERS.keySet().stream().anyMatch( key -> key.isAssignableFrom( value.getClass() ) );
	}

	public static String convert( Object value, String pattern )
	{
		return FORMATTERS.entrySet().stream().filter( entry -> entry.getKey().isAssignableFrom( value.getClass() ) )
				.map( Map.Entry::getValue ).map( converter -> ((ObjectFormatter<Object>) converter).format( value, pattern ) ).findFirst()
				.orElseThrow( () -> new IllegalArgumentException( "Não existe conversor para a classe " + value.getClass().getSimpleName() ) );
	}

	public static String convertIfNecessary( QueryColumn column, Object value )
	{
		if ( value == null )
		{
			return null;
		}
		if ( needsConversionToString( value ) )
		{
			return convert( value, column.getDateTimePattern() );
		}
		else
		{
			return value.toString();
		}
	}
}
