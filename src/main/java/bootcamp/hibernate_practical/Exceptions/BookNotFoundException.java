package bootcamp.hibernate_practical.Exceptions;

public class BookNotFoundException extends Exception{
    public BookNotFoundException (Long id){
        super ("Sorry, we can't find book with id " + id);
    }
}
