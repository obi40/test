package com.optimiza.ehope.web.util;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.jasperreports.JasperReportsPdfView;

import com.optimiza.core.admin.model.SecTenant;
import com.optimiza.core.admin.service.SecTenantService;
import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.helper.SearchCriterion.FilterOperator;
import com.optimiza.core.common.util.SecurityUtil;
import com.optimiza.core.common.util.SpringUtil;
import com.optimiza.core.lkp.helper.PrintFormat;
import com.optimiza.ehope.lis.wrapper.VisitResultsWrapper;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.SimplePdfReportConfiguration;
import net.sf.jasperreports.export.SimpleRtfExporterConfiguration;
import net.sf.jasperreports.export.SimpleRtfReportConfiguration;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;
import net.sf.jasperreports.export.WriterExporterOutput;

@Component
public class ReportUtil {

	@Autowired
	private SecTenantService secTenantService;

	private static SecTenantService tenantService;

	public static Class<?> clazz;//so it can be used in a static way

	public ReportUtil() {
		clazz = getClass();
	}

	@PostConstruct
	public void initializing() {
		tenantService = secTenantService;
	}

	/**
	 * Create jasper report as pdf.
	 * 
	 * @param reportName
	 * @return JasperReportsPdfView
	 */
	public static JasperReportsPdfView createJasperView(String reportName) {
		JasperReportsPdfView view = new JasperReportsPdfView();
		view.setReportDataKey("datasource");
		view.setUrl("classpath:reports/" + reportName + ".jrxml");
		view.setApplicationContext(SpringUtil.getApplicationContext());
		view.setContentType(MediaType.APPLICATION_PDF_VALUE);
		return view;
	}

	private static URL getReportURL(String reportName) {
		return clazz.getResource("/reports/" + reportName + ".jrxml");
	}

	/**
	 * Create jasper report as byte[].
	 * 
	 * @param reportName
	 * @param jrCollectionDataSource
	 * @param params
	 * @return byte[]
	 * @throws JRException
	 */
	public static byte[] createJasperBytesPDF(String reportName, JRBeanCollectionDataSource jrCollectionDataSource,
			Map<String, Object> params) throws JRException {
		URL reportPath = getReportURL(reportName);
		JasperReport jreport = JasperCompileManager.compileReport(reportPath.getPath());
		JasperPrint jasperPrint = JasperFillManager.fillReport(jreport, params, jrCollectionDataSource);
		return JasperExportManager.exportReportToPdf(jasperPrint);
	}

	/**
	 * Generate multiple reports by using reportsData
	 * 
	 * @param response
	 * @param reportName
	 * @param reportsData
	 * @throws JRException
	 * @throws IOException
	 */
	public static void createMultipleJasperViews(HttpServletResponse response, String reportName,
			Map<JRDataSource, Map<String, Object>> reportsData) {

		PrintFormat printFormat = PrintFormat.valueOf(
				tenantService.findOne(SearchCriterion.generateRidFilter(SecurityUtil.getCurrentUser().getTenantId(), FilterOperator.eq),
						SecTenant.class).getPrintFormat().getCode());

		createMultipleJasperViews(response, reportName, reportsData, printFormat.getValue());

	}

	public static void createMultipleJasperViews(HttpServletResponse response, String reportName,
			Map<JRDataSource, Map<String, Object>> reportsData, String reportFormat) {

		try {
			URL reportPath = getReportURL(reportName);
			List<JasperPrint> jpList = new ArrayList<>();
			for (Map.Entry<JRDataSource, Map<String, Object>> entry : reportsData.entrySet()) {
				JasperReport jreport = JasperCompileManager.compileReport(reportPath.getPath());
				JasperPrint jprint = JasperFillManager.fillReport(jreport, entry.getValue(), entry.getKey());
				jpList.add(jprint);
			}

			PrintFormat printFormat = PrintFormat.valueOf(reportFormat);
			switch (printFormat) {
				case PDF:
					JRPdfExporter exporter = new JRPdfExporter();
					OutputStream output = response.getOutputStream();
					response.setContentType(MediaType.APPLICATION_PDF_VALUE);
					exporter.setExporterInput(SimpleExporterInput.getInstance(jpList));
					exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(output));

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
					break;
				case RTF:
					JRRtfExporter exporterRtf = new JRRtfExporter();
					OutputStream outputStream = response.getOutputStream();
					response.setContentType("application/rtf");

					WriterExporterOutput outputRtf = new SimpleWriterExporterOutput(outputStream);

					exporterRtf.setExporterInput(SimpleExporterInput.getInstance(jpList));
					exporterRtf.setExporterOutput(outputRtf);

					SimpleRtfExporterConfiguration rtfExpConf = new SimpleRtfExporterConfiguration();
					exporterRtf.setConfiguration(rtfExpConf);

					SimpleRtfReportConfiguration rtfRepConf = new SimpleRtfReportConfiguration();
					exporterRtf.setConfiguration(rtfRepConf);

					exporterRtf.exportReport();

					break;
				default:
					break;

			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

	}

	/**
	 * Generate multiple reports by using reportsData
	 * 
	 * @param response
	 * @param wrappersList
	 * @throws JRException
	 * @throws IOException
	 */
	public static void createMultipleJasperViews(HttpServletResponse response, List<VisitResultsWrapper> wrappersList,
			String namePrimary) {
		PrintFormat printFormat = PrintFormat.valueOf(
				tenantService.findOne(SearchCriterion.generateRidFilter(SecurityUtil.getCurrentUser().getTenantId(), FilterOperator.eq),
						SecTenant.class).getPrintFormat().getCode());
		createMultipleJasperViews(response, wrappersList, namePrimary, printFormat.getValue());
	}

	/**
	 * Generate multiple reports by using reportsData
	 * 
	 * @param response
	 * @param wrappersList
	 * @throws JRException
	 * @throws IOException
	 */
	public static void createMultipleJasperViews(HttpServletResponse response, List<VisitResultsWrapper> wrappersList,
			String namePrimary, String reportFormat) {
		try {
			List<JasperPrint> jpList = new ArrayList<>();

			for (VisitResultsWrapper visitResult : wrappersList) {
				Map<String, Object> params = new HashMap<>();
				params.put("wrapper", visitResult);
				params.put("namePrimary", namePrimary);
				//params.put(JRParameter.REPORT_TIME_ZONE, DateUtil.returnClientTimeZone(timezoneId, timezoneOffset));

				JRDataSource ds = new JRBeanCollectionDataSource(Arrays.asList(visitResult));
				URL reportPath = getReportURL(visitResult.getReportName());
				JasperReport jreport = JasperCompileManager.compileReport(reportPath.getPath());
				JasperPrint jprint = JasperFillManager.fillReport(jreport, params, ds);
				jpList.add(jprint);
			}

			PrintFormat printFormat = PrintFormat.valueOf(reportFormat);
			switch (printFormat) {
				case PDF:
					JRPdfExporter exporter = new JRPdfExporter();
					OutputStream output = response.getOutputStream();
					response.setContentType(MediaType.APPLICATION_PDF_VALUE);
					exporter.setExporterInput(SimpleExporterInput.getInstance(jpList));
					exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(output));

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
					break;
				case RTF:
					JRRtfExporter exporterRtf = new JRRtfExporter();
					OutputStream outputStream = response.getOutputStream();
					response.setContentType("application/rtf");

					WriterExporterOutput outputRtf = new SimpleWriterExporterOutput(outputStream);

					exporterRtf.setExporterInput(SimpleExporterInput.getInstance(jpList));
					exporterRtf.setExporterOutput(outputRtf);

					SimpleRtfExporterConfiguration rtfExpConf = new SimpleRtfExporterConfiguration();
					exporterRtf.setConfiguration(rtfExpConf);

					SimpleRtfReportConfiguration rtfRepConf = new SimpleRtfReportConfiguration();
					exporterRtf.setConfiguration(rtfRepConf);

					exporterRtf.exportReport();

					break;
				default:
					break;

			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

	}

}
