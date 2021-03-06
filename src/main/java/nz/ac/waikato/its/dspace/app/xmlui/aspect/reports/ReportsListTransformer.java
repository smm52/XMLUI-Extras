package nz.ac.waikato.its.dspace.app.xmlui.aspect.reports;

import nz.ac.waikato.its.dspace.reporting.ReportConfigurationService;
import nz.ac.waikato.its.dspace.reporting.configuration.ConfigurationException;
import nz.ac.waikato.its.dspace.reporting.configuration.Report;
import org.apache.cocoon.ProcessingException;
import org.apache.log4j.Logger;
import org.dspace.app.xmlui.cocoon.AbstractDSpaceTransformer;
import org.dspace.app.xmlui.utils.UIException;
import org.dspace.app.xmlui.wing.Message;
import org.dspace.app.xmlui.wing.WingException;
import org.dspace.app.xmlui.wing.element.*;
import org.dspace.authorize.AuthorizeException;
import org.dspace.core.ConfigurationManager;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Stefan Mutter (stefanm@waikato.ac.nz) for University of Waikato, ITS
 */
public class ReportsListTransformer extends AbstractDSpaceTransformer {
    private static final Logger log = Logger.getLogger(ReportsListTransformer.class);

    private static final Message T_standard_report = message("uow.aspects.Reports.ReportsListTransformer.home_head");
    private static final Message T_title = message("uow.aspects.Reports.ReportsListTransformer.title");
    private static final Message T_dspace_home = message("xmlui.general.dspace_home");
    private static final Message T_trail = message("uow.aspects.Reports.ReportsListTransformer.trail");
    private static final Message T_success = message("uow.aspects.Reports.success");
    private static final Message T_fail = message("uow.aspects.Reports.fail");
    private static final Message T_report_loading_fail = message("uow.aspects.Reports.ReportsListTransformer.fail");
	private static final Message T_intro = message("uow.aspects.Reports.ReportsListTransformer.intro");

	@Override
    public void addBody(Body body) throws SAXException, WingException, UIException, SQLException, IOException, AuthorizeException, ProcessingException {
        Division reportHome = body.addDivision("standard-report-home");
        Division report = reportHome.addDivision("standard-report-div","standard-report");
        report.setHead(T_standard_report);

        Division div = report.addInteractiveDivision("standard-report-list", contextPath + "/reports/standard", Division.METHOD_POST);

		div.addPara(T_intro);

        String configDir = ConfigurationManager.getProperty("dspace.dir") + "/config/modules/reporting";
        ReportConfigurationService configurationService = new ReportConfigurationService(configDir);
        try {
            List<String> reportNames = configurationService.getCannedReportNames();
            for (String reportName : reportNames) {
	            Report requestedReport = configurationService.getCannedReportConfiguration(reportName);
	            String reportId = requestedReport.getId();
	            Division reportEntry = div.addDivision("report-info-" + reportId, "report-info panel panel-default");
	            String reportTitle = requestedReport.getTitle();
	            reportEntry.addDivision("report-title-" + reportId, "panel-heading").addPara("report-link-" + reportId, "panel-title").addXref(contextPath + "/reports/standard/" + reportId, reportTitle);
	            ReportUtils.addReportEntry(reportEntry, requestedReport);
            }
        } catch (ConfigurationException e) {
            log.error("Unable to show list of all standard reports",e);
            div.addPara(T_report_loading_fail);
        }
    }

	@Override
    public void addPageMeta(PageMeta pageMeta) throws SAXException, WingException, UIException, SQLException, IOException, AuthorizeException {
        pageMeta.addMetadata("title").addContent(T_title);
        pageMeta.addTrailLink(contextPath + "/",T_dspace_home);
        pageMeta.addTrail().addContent(T_trail);
    }
}
