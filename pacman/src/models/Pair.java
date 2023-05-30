/**
 * Created by Stephen Zhang
 */

package src.models;

/**
 * Why Java still doesn't have tuple in 2023???
 * @param item1 first item
 * @param item2 second item
 * @param <T> first item type
 * @param <U> second item type
 */
public record Pair<T, U>(T item1, U item2) {}