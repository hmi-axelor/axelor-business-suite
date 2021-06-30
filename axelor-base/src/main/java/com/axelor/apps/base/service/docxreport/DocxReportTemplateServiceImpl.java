package com.axelor.apps.base.service.docxreport;

import com.axelor.apps.base.db.PrintTemplate;
import com.axelor.db.JpaRepository;
import com.axelor.db.Model;
import com.axelor.db.mapper.Mapper;
import com.axelor.meta.MetaFiles;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.docx4j.Docx4J;
import org.docx4j.convert.out.HTMLSettings;
import org.docx4j.model.fields.merge.DataFieldName;
import org.docx4j.model.fields.merge.MailMerger;
import org.docx4j.model.fields.merge.MailMerger.OutputField;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

public class DocxReportTemplateServiceImpl implements DocxReportTemplateService {

  @Override
  public File createReport(List<Long> objectIds, PrintTemplate printTemplate)
      throws ClassNotFoundException, IOException, Exception {

    File file = MetaFiles.getPath(printTemplate.getDocxTemplate()).toFile();
    /*WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
    NumberingDefinitionsPart ndp = new NumberingDefinitionsPart();
    wordMLPackage.getMainDocumentPart().addTargetPart(ndp);
    ndp.unmarshalDefaultNumbering();*/
    // XHTMLImporterImpl xHTMLImporter = new XHTMLImporterImpl(wordMLPackage);
    // xHTMLImporter.setHyperlinkStyle("Hyperlink");
    WordprocessingMLPackage docx = WordprocessingMLPackage.load(file);
    // AbstractHtmlExporter exporter = new HtmlExporterNG2();
    File newFile = new File("test.html");
    OutputStream os = new FileOutputStream(newFile);
    HTMLSettings htmlSettings = new HTMLSettings();
    htmlSettings.setWmlPackage(docx);

    List<Model> modelList = getModelData(printTemplate.getMetaModel().getFullName(), objectIds);

    List<Map<DataFieldName, String>> data = getData(modelList);

    MailMerger.setMERGEFIELDInOutput(OutputField.KEEP_MERGEFIELD);

    // int x = 0;
    for (Map<DataFieldName, String> docMapping : data) {
      MailMerger.performMerge(docx, docMapping, true);
      // docx.save(new java.io.File("C:/temp/OUT__MAIL_MERGE_" + x++ + ".docx"));
    }

    // StreamResult result = new StreamResult(os);
    Docx4J.toHTML(htmlSettings, os, Docx4J.FLAG_EXPORT_PREFER_XSL);
    return newFile;
  }

  @SuppressWarnings("unchecked")
  protected <T extends Model> List<T> getModelData(String modelFullName, List<Long> ids)
      throws ClassNotFoundException {
    Class<T> modelClass = (Class<T>) Class.forName(modelFullName);
    return JpaRepository.of(modelClass).all().filter("id in :ids").bind("ids", ids).fetch();
  }

  protected List<Map<DataFieldName, String>> getData(List<Model> modelList) {
    List<Map<DataFieldName, String>> data = new ArrayList<Map<DataFieldName, String>>();
    for (Model model : modelList) {
      Map<String, Object> modelMap = Mapper.toMap(model);
      Map<DataFieldName, String> map1 = new HashMap<DataFieldName, String>();
      for (Map.Entry<String, Object> entry : modelMap.entrySet()) {
        map1.put(new DataFieldName(entry.getKey()), entry.getValue().toString());
      }
      data.add(map1);
    }
    return data;
  }
}
