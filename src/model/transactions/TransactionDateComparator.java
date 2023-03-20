package model.transactions;

import java.util.Comparator;

public class TransactionDateComparator implements Comparator<AbstractTransaction> {
  @Override
  public int compare(AbstractTransaction o1, AbstractTransaction o2) {
    return o1.getDate().compareTo(o2.getDate());
  }
}
