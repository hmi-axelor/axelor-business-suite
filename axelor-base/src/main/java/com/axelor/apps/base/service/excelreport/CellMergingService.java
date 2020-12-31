/*
 * Axelor Business Solutions
 *
 * Copyright (C) 2020 Axelor (<http://axelor.com>).
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
package com.axelor.apps.base.service.excelreport;

import com.axelor.db.mapper.Mapper;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

public interface CellMergingService {

  public int setMergeOffset(
      Map<Integer, Map<String, Object>> inputMap,
      Mapper mapper,
      List<CellRangeAddress> mergedCellsRangeAddressList);

  public Set<CellRangeAddress> getBlankMergedCells(
      Sheet originSheet, List<CellRangeAddress> mergedCellsRangeAddressList, String sheetType);

  public CellRangeAddress setMergedCellsForTotalRow(
      List<CellRangeAddress> mergedCellsRangeAddressList,
      int rowIndex,
      int columnIndex,
      int totalRecord);

  public ImmutablePair<CellRangeAddress, CellRangeAddress> shiftMergedRegion(
      List<CellRangeAddress> mergedCellsRangeAddressList,
      int rowIndex,
      int columnIndex,
      int offset);

  public void shiftMergedRegions(Set<CellRangeAddress> mergedRegionsAddressList, int offset);
}
