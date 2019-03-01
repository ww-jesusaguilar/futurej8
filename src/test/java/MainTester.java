
import com.futuresj8.example.Example;

import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class MainTester{
    public static void main(String[]args){
        Scanner sc = new Scanner(System.in);
        int caso = sc.nextInt();
        while(caso != 0) {
            try {
                switch (caso) {
                    case 1:
                        Example.ejecutarRunnableSimple();
                        break;
                    case 2:
                        Example.ejecutarCallable();
                        break;
                    case 3:
                        Example.ejecutarListaCallables();
                        break;
                    case 4:
                        Example.ejecutarRunnableLambda();
                        break;
                    case 5:
                        Example.ejecutarExecutor();
                        break;
                    case 6:
                        Example.ejecutarFutureCallable();
                        break;
                    case 7:
                        Example.ejecutarFutureTerminar();
                        break;
                    case 8:
                        Example.ejecutarCompletableFuture();
                        break;
                    case 9:
                        Example.ejecutarCompletableFutureCombined();
                        break;
                    case 10:
                        Example.ejecutarListenableFuture();
                        break;
                    case 11:
                        Example.ejecutarListenableFuturesEncadenados();
                        break;
                }
            } catch (InterruptedException ie) {

            } catch (ExecutionException ee) {

            }
            caso = sc.nextInt();
        }
        return ;
    }
}