package br.com.digitalhouse.rxjavaapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import java.util.Arrays;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    TextView textViewMensagem;
    Button btnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewMensagem = findViewById(R.id.textViewMensagem);
        btnStart = findViewById(R.id.btnStart);

        btnStart.setOnClickListener(v -> iniciandoComRXJava());
        iniciandoComRXJava();
    }

    private void iniciandoComRXJava() {

        // Observable é um objeto que trabalha com emissão de eventos, ou seja,
        // ele emite estados em que se encontra, para que estes estados possam ser observados por outros elementos,
        // Ex: o subscribe

        // Criando um observable e fazendo o subscribe
        Observable.range(0, 100) // Emite uma sequencia de inteiros em um range especificado.
                .map(numero -> numero * 2) // mapeia cada evento emitido multiplicando seu valor por 2
                .filter(numero -> numero % 2 == 0) // filtra os eventos que tiverem valor par
                .subscribe(numero -> {
                    System.out.println(numero); // imprime no console o valor do evento que foi observado
                });


        Observable.just(1, 2) // emite 2 eventos
                .subscribe(System.out::println);

        // Emitindo eventos de erro
        Observable.error(new NullPointerException("Não pode ser nulo"));
        //.subscribe(System.out::println);

        // Emitindo eventos apartir de uma lista, e filtrando o nome
        Observable.fromIterable(Arrays.asList("Tairo", "Jessica", "Tadashi"))
                .filter(s -> s.equals("Tairo"))
                .map(s -> "Nome: " + s)
                .subscribe(System.out::println);

        // Cold/Lazy observable -> não emitem eventos enquanto não tiverem atrelado a o um subscriber
        // Ou seja se não tiver niguem observando eles, eles não fazem nada.

        // Hot observable emite os eventos assim qeu é criado
        Observable.fromIterable(Arrays.asList("Tairo", "Jessica", "Tadashi"))
                .filter(s -> s.equals("Tairo"))
                .map(s -> "Nome: " + s);


        // Forma mas clara e dinamica de criar Observables completos é usando o create
        Observable<Integer> integerObservable = Observable.create(emitter -> {
            emitter.onNext(1);
            emitter.onNext(2);
            emitter.onNext(3);
            //emitter.onError(new Exception("Deu ruim !"));
            //emitter.onComplete();

            // buscar na base de dados e emintir cada item no onNext
            // Aqui poderiamos colocar try/catch e colocar um aexceção no onError
            // e depois chamar o onConplete quando acabassemos
        });


        // Subscribe apenas com onNext
        integerObservable.subscribe(integer -> {
            System.out.println(integer);
        });

        // Subscribe apenas com onNext e onError
        integerObservable.subscribe(integer -> {
            System.out.println(integer);
        }, throwable -> {
            System.out.println(throwable.getMessage());
        });

        // Subscribe apenas com onNext, onError e onComplete
        integerObservable.subscribe(integer -> {
            System.out.println(integer);
        }, throwable -> {
            System.out.println(throwable.getMessage());
        }, () -> {
            System.out.println("Completou, não emite mais eventos");
        });


        /// OPERADORES -> formas de interceptar os eventos emitidos
        // filter -> filtra os eventos baseado em um condição
        // skip -> pula eventos
        // map -> maeia cada evento, podendo transformar seu valor
        // flatmap

        // Flatmap usado quando quero depois da amissão de um evento quisermos retornar um novo observable
        Observable<String> stringObservable = Observable.just("Aqui é um teste com flatmap");
        Observable.just(10)
                .flatMap(numero -> {
                    System.out.println("Nostrando o número: " + numero);
                    return stringObservable;
                })
                .subscribe(System.out::println);

        // So são executados quando há um subscribre


        // Schedulers
        Observable.just(10)
                .subscribeOn(Schedulers.io())
                .flatMap(numero -> {
                    System.out.println("Thread:" + Thread.currentThread().getName());
                    return stringObservable;
                })
                .subscribeOn(Schedulers.newThread())
                .subscribe(s -> {
                    textViewMensagem.setText("Texto do numero: " + s);
                });

    }
}