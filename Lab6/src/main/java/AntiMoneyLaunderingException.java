//This exception is used to keep users from depositing more than $5000 per day
public class AntiMoneyLaunderingException extends Exception {
  public AntiMoneyLaunderingException() {
      super();
  } 
  public AntiMoneyLaunderingException(String message) {
    super(message);
}

public AntiMoneyLaunderingException(String message, Throwable cause) {
    super(message, cause);
}

public AntiMoneyLaunderingException(Throwable cause) {
    super(cause);
}

}
