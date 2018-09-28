package br.com.eits.queryexport;

import java.util.Set;

import br.com.eits.queryexport.exporter.CSVExporter;
import br.com.eits.queryexport.exporter.Exporter;
import br.com.eits.queryexport.exporter.PDFExporter;
import br.com.eits.queryexport.exporter.XLSExporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;

@Configuration
public class QueryExportBeanConfig
{
	@Bean
	public Exporter pdfExporter( QueryExportConfiguration queryExportConfiguration, MessageSource messageSource )
	{
		return new PDFExporter( queryExportConfiguration, messageSource );
	}

	@Bean
	public Exporter csvExporter( MessageSource messageSource )
	{
		return new CSVExporter( messageSource );
	}

	@Bean
	public Exporter xlsExporter( MessageSource messageSource )
	{
		return new XLSExporter( messageSource );
	}

	@Bean
	public QueryExporter queryExporter( @Autowired(required = false) Set<JpaRepository<?, ?>> jpaRepositories, Set<Exporter> exporters, MessageSource messageSource )
	{
		return new QueryExporter( jpaRepositories, exporters, messageSource );
	}
}
