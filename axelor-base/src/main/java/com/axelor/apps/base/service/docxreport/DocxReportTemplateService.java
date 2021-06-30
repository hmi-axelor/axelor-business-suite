package com.axelor.apps.base.service.docxreport;

import com.axelor.apps.base.db.PrintTemplate;
import java.io.File;
import java.io.IOException;
import java.util.List;

public interface DocxReportTemplateService {

  public File createReport(List<Long> objectIds, PrintTemplate printTemplate)
      throws ClassNotFoundException, IOException, Exception;
}
