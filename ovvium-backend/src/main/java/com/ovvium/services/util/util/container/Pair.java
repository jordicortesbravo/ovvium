package com.ovvium.services.util.util.container;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pair<F, S> implements Comparable<Pair<F, S>> {

    public static <T, T2> Pair<T, T2> makePair(T first, T2 second) {
        return new Pair<T, T2>(first, second);
    }

    @Getter
    private F first;

    @Getter
    private S second;

    /**
     * No podemos asumir que los elementos sean comparables, así que el algoritmo será
     * 
     * 1- si el primer elemento es comparable, se devuelve el compareto de los primeros elementos. si son iguales, miramos el segundo. 2- si
     * no es comparable, miramos el segundo. 3- si el segundo elemento es comparable, devolvemos el compareTo de los segundos elementos. si
     * son iguales, devolvemos diferencia de hashCodes 4- si no es comparable, devolvemos diferencia de hashCodes
     */
    @SuppressWarnings("unchecked")
    @Override
    public int compareTo(Pair<F, S> other) {
        if (other.getFirst() instanceof Comparable && ((Comparable<F>) this.getFirst()).compareTo(other.getFirst()) != 0) {
            return ((Comparable<F>) this.getFirst()).compareTo(other.getFirst());
        }

        if (other.getSecond() instanceof Comparable && ((Comparable<S>) this.getSecond()).compareTo(other.getSecond()) != 0) {
            return ((Comparable<S>) this.getSecond()).compareTo(other.getSecond());
        }

        return this.hashCode() - other.hashCode();
    }
}
