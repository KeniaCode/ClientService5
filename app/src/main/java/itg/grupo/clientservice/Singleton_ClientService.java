package itg.grupo.clientservice;

/**
 * Created by kenia on 3/02/17.
 */
public class Singleton_ClientService {
    private static Singleton_ClientService ourInstance = new Singleton_ClientService();

    public static Singleton_ClientService getInstance() {
        return ourInstance;
    }

    private Singleton_ClientService() {
    }

    public static int id_usuario = 0;
}
