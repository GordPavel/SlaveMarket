package exceptions;

@Deprecated
public class WrongQueryException extends IllegalArgumentException {

    public WrongQueryException(String query) {
        super("Illegal query \"" + query + "\"");
    }

}
