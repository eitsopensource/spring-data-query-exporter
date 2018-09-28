package br.com.eits.queryexport;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class DummyEntity
{
	private final Long id;
	private final String name;
	private final Boolean enabled;
	private final BigDecimal price;
	private final DummyEntityStatus status;

	public enum DummyEntityStatus
	{
		PENDING, AUTHORIZED, DENIED
	}
}
