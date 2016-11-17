/*   This file is part of My Expenses.
 *   My Expenses is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   My Expenses is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with My Expenses.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.totschnig.myexpenses;

import java.math.BigDecimal;
import java.util.Currency;

public class Money {
  private Currency currency;
  private Long amountMinor;
  
  public Money(Currency currency, Long amountMinor) {
    this.currency = currency;
    this.amountMinor = amountMinor;
  }
  public Currency getCurrency() {
    return currency;
  }
  public void setCurrency(Currency currency) {
    this.currency = currency;
  }
  public Long getAmountMinor() {
    return amountMinor;
  }
  public void setAmountMinor(Long amountMinor) {
    this.amountMinor = amountMinor;
  }
  public void setAmountMajor(BigDecimal amountMajor) {
    int scale = currency.getDefaultFractionDigits();
    this.amountMinor = amountMajor.multiply(new BigDecimal(Math.pow(10,scale))).longValue();
  }
  public BigDecimal getAmountMajor() {
    BigDecimal bd = new BigDecimal(amountMinor);
    int scale = currency.getDefaultFractionDigits();
    if (scale != -1) {
      bd.setScale(scale);
      return bd.divide(new BigDecimal(Math.pow(10,scale)));
    }
    return bd;
  }
  public boolean equals(Money other) {
    return currency == other.currency  && amountMinor == other.amountMinor;
  }
}
