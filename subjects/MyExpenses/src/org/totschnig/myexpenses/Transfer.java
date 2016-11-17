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

/**
 * a transfer consists of a pair of transactions, one for each account
 * this class handles creation and update
 * @author Michael Totschnig
 *
 */
public class Transfer extends Transaction {
  private static ExpensesDbAdapter mDbHelper  = MyApplication.db();
  
  public Transfer(long accountId,long amount) {
    super(accountId,amount);
  }
  public Transfer(long accountId, Money amount) {
    super(accountId,amount);
  }
  public static boolean delete(long id,long peer) {
    return mDbHelper.deleteTransfer(id,peer);
  }

  /* (non-Javadoc)
   * @see org.totschnig.myexpenses.Transaction#save()
   */
  public long save() {
    if (id == 0) {
      long ids[] = mDbHelper.createTransfer(dateAsString, amount.getAmountMinor(), comment,catId,accountId);
      id = ids[0];
      transfer_peer = ids[1];
    } else {
      mDbHelper.updateTransfer(id, dateAsString, amount.getAmountMinor(), comment,catId);
    }
    return id;
  }
}
