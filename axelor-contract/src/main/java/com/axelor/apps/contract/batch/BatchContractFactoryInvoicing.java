/*
 * Axelor Business Solutions
 *
 * Copyright (C) 2021 Axelor (<http://axelor.com>).
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License, version 3,
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.axelor.apps.contract.batch;

import com.axelor.apps.base.db.AppContract;
import com.axelor.apps.base.db.Batch;
import com.axelor.apps.base.service.app.AppBaseService;
import com.axelor.apps.base.service.app.AppService;
import com.axelor.apps.contract.db.Contract;
import com.axelor.apps.contract.db.repo.ContractRepository;
import com.axelor.apps.contract.exception.IExceptionMessage;
import com.axelor.apps.contract.service.ContractService;
import com.axelor.apps.message.db.Message;
import com.axelor.apps.message.db.Template;
import com.axelor.apps.message.service.MessageService;
import com.axelor.apps.message.service.TemplateMessageService;
import com.axelor.db.Query;
import com.axelor.exception.AxelorException;
import com.axelor.exception.db.repo.TraceBackRepository;
import com.axelor.exception.service.TraceBackService;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.google.inject.Inject;
import java.time.format.DateTimeFormatter;

public class BatchContractFactoryInvoicing extends BatchContractFactory {

  protected TemplateMessageService templateMessageService;
  protected MessageService messageService;

  @Inject
  public BatchContractFactoryInvoicing(
      ContractRepository repository,
      ContractService service,
      AppBaseService baseService,
      TemplateMessageService templateMessageService,
      MessageService messageService) {
    super(repository, service, baseService);
    this.templateMessageService = templateMessageService;
    this.messageService = messageService;
  }

  @Override
  public Query<Contract> prepare(Batch batch) {
    return repository
        .all()
        .filter(
            "self.isInvoicingManagement = TRUE "
                + "AND self.currentContractVersion.automaticInvoicing = TRUE "
                + "AND self.invoicingDate <= :date "
                + "AND :batch NOT MEMBER of self.batchSet")
        .bind(
            "date",
            baseService
                .getTodayDate(batch.getContractBatch().getCompany())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
        .bind("batch", batch);
  }

  @Override
  public void process(Contract contract) throws AxelorException {
    service.invoicingContract(contract);
    sendEmail(contract);
  }

  public void sendEmail(Contract contract) throws AxelorException {
    AppContract contractApp = (AppContract) Beans.get(AppService.class).getApp("contract");
    Template template = contractApp.getContractInvoicingReminderTemplate();
    if (template != null) {
      String model = template.getMetaModel().getFullName();
      String tag = template.getMetaModel().getName();
      try {
        Message message =
            templateMessageService.generateMessage(contract.getId(), model, tag, template);
        messageService.sendByEmail(message);
      } catch (Exception e) {
        TraceBackService.trace(e);
      }
    } else {
      throw new AxelorException(
          TraceBackRepository.CATEGORY_MISSING_FIELD,
          I18n.get(IExceptionMessage.TEMPLATE_NOT_DEFINED));
    }
  }
}
