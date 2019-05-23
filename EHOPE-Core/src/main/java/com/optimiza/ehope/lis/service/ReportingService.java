package com.optimiza.ehope.lis.service;

import java.io.File;
import java.io.InputStream;
import java.sql.SQLException;

import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRSaver;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.SimplePdfReportConfiguration;

/**
 * ReportingService.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since May/30/2018
 **/
@Service("ReportingService")
public class ReportingService {

	//	@Autowired
	//	private DataSource dataSource;

	/**
	 * 
	 * @param jrxmlFile Default path is [EHOPE-Core\src\main\resources\example.jrxml] Cannot be null
	 * @param jasperFile Default output is [EHOPE-WEB\example.jasper] Cannot be null
	 * @throws JRException
	 */
	public void compileAndSaveReport(String jrxmlFile, String jasperFile) throws JRException {
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(jrxmlFile);
		JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
		JRSaver.saveObject(jasperReport, jasperFile);
	}

	/**
	 * 
	 * @param jasperFile Default path is [EHOPE-WEB\example.jasper] Cannot be null
	 * @throws JRException
	 * @throws SQLException
	 */
	public JasperPrint fillSavedReport(String jasperFile) throws JRException, SQLException {
		JasperReport jasperReport = (JasperReport) JRLoader.loadObject(new File(jasperFile));
		//dataSource.getConnection();
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, new JREmptyDataSource());
		return jasperPrint;
	}

	/**
	 * 
	 * @param jasperPrint
	 * @param pdfFile Default output is [EHOPE-WEB\example.pdf] Cannot be null
	 * @throws JRException
	 */
	public void exportPdfReport(JasperPrint jasperPrint, String pdfFile) throws JRException {
		JRPdfExporter exporter = new JRPdfExporter();

		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(pdfFile));

		SimplePdfReportConfiguration reportConfig = new SimplePdfReportConfiguration();
		reportConfig.setSizePageToContent(true);
		reportConfig.setForceLineBreakPolicy(false);

		SimplePdfExporterConfiguration exportConfig = new SimplePdfExporterConfiguration();
		exportConfig.setMetadataAuthor("Optimiza");
		exportConfig.setEncrypted(true);
		exportConfig.setAllowedPermissionsHint("PRINTING");

		exporter.setConfiguration(reportConfig);
		exporter.setConfiguration(exportConfig);

		exporter.exportReport();
	}

}
