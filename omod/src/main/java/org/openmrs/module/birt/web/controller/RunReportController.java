package org.openmrs.module.birt.web.controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.birt.BirtReport;
import org.openmrs.module.birt.BirtReportException;
import org.openmrs.module.birt.BirtReportService;
import org.openmrs.web.WebConstants;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;


public class RunReportController extends SimpleFormController {
	
    /** Logger for this class and subclasses */
    protected final Log log = LogFactory.getLog(getClass());

	/**
	 * 
	 * Allows for Integers to be used as values in input tags.
	 *   Normally, only strings and lists are expected 
	 * 
	 * @see org.springframework.web.servlet.mvc.BaseCommandController#initBinder(javax.servlet.http.HttpServletRequest, org.springframework.web.bind.ServletRequestDataBinder)
	 */
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		super.initBinder(request, binder);
        binder.registerCustomEditor(java.lang.Integer.class, new CustomNumberEditor(java.lang.Integer.class, true));
	}

	/** 
	 * 
	 * The onSubmit function receives the form/command object that was modified
	 *   by the input form and saves it to the db
	 * 
	 * @see org.springframework.web.servlet.mvc.SimpleFormController#onSubmit(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
	 */
	protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object obj, BindException errors) throws Exception {		
		String view = getFormView();
		BirtReport report = (BirtReport)obj;
		log.debug("Birt report object: " + report);
		BirtReportService reportService = (BirtReportService)Context.getService(BirtReportService.class);
		reportService.generateReport(report);
		
		view = getSuccessView();
		request.getSession().setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Report.saved");
		
		return new ModelAndView(new RedirectView(view));
	}	

	/**
	 * This is called prior to displaying a form for the first time.  It tells Spring
	 *   the form/command object to load into the request
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
    protected Object formBackingObject(HttpServletRequest request) throws ServletException {

		BirtReport report = null;
		
		// Get the requested report
    	String reportId = request.getParameter("reportId");
    	if (reportId != null) {   		
    		BirtReportService reportService = (BirtReportService)Context.getService(BirtReportService.class);
    		report = reportService.getReport(Integer.valueOf(reportId));
    	}
		
    	// TODO This should throw an exception 
		if (report == null) { 
			throw new BirtReportException("Could not find report " + reportId);
		}
		
		
        return report;
    }
    
}