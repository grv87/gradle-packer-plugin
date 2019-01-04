package org.fidata;

import sun.misc.SharedSecrets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;

public class CustomEnumSet32<E extends Enum<E>> implements Set<E> {
  /**
   * The class of all the elements of this set.
   */
  final Class<E> elementType;

  /**
   * All of the values comprising T.  (Cached for performance.)
   */
  final Enum<?>[] universe;

  private static Enum<?>[] ZERO_LENGTH_ENUM_ARRAY = new Enum<?>[0];

  CustomEnumSet32(Class<E>elementType, Enum<?>[] universe) {
    this.elementType = elementType;
    this.universe    = universe;
  }

  /**
   * Creates an empty enum set with the specified element type.
   *
   * @param <E> The class of the elements in the set
   * @param elementType the class object of the element type for this enum
   *     set
   * @return An empty enum set of the specified type.
   * @throws NullPointerException if <tt>elementType</tt> is null
   */
  public static <E extends Enum<E>> CustomEnumSet32<E> noneOf(Class<E> elementType) {
    Enum<?>[] universe = getUniverse(elementType);
    if (universe == null)
      throw new ClassCastException(elementType + " not an enum");

    if (universe.length <= 32)
      return new CustomEnumSet32<E>(elementType, universe);
    else
      throw new IllegalArgumentException("CustomEnumSet32 supports enums with no more than 32 items");
  }

  /**
   * Creates an enum set containing all of the elements in the specified
   * element type.
   *
   * @param <E> The class of the elements in the set
   * @param elementType the class object of the element type for this enum
   *     set
   * @return An enum set containing all the elements in the specified type.
   * @throws NullPointerException if <tt>elementType</tt> is null
   */
  public static <E extends Enum<E>> CustomEnumSet32<E> allOf(Class<E> elementType) {
    CustomEnumSet32<E> result = noneOf(elementType);
    result.addAll();
    return result;
  }

  /**
   * Creates an enum set with the same element type as the specified enum
   * set, initially containing the same elements (if any).
   *
   * @param <E> The class of the elements in the set
   * @param s the enum set from which to initialize this enum set
   * @return A copy of the specified enum set.
   * @throws NullPointerException if <tt>s</tt> is null
   */
  public static <E extends Enum<E>> CustomEnumSet32<E> copyOf(CustomEnumSet32<E> s) {
    return s.clone();
  }

  /**
   * Creates an enum set initialized from the specified collection.  If
   * the specified collection is an <tt>CustomEnumSet32</tt> instance, this static
   * factory method behaves identically to {@link #copyOf(CustomEnumSet32)}.
   * Otherwise, the specified collection must contain at least one element
   * (in order to determine the new enum set's element type).
   *
   * @param <E> The class of the elements in the collection
   * @param c the collection from which to initialize this enum set
   * @return An enum set initialized from the given collection.
   * @throws IllegalArgumentException if <tt>c</tt> is not an
   *     <tt>CustomEnumSet32</tt> instance and contains no elements
   * @throws NullPointerException if <tt>c</tt> is null
   */
  public static <E extends Enum<E>> CustomEnumSet32<E> copyOf(Collection<E> c) {
    if (c instanceof CustomEnumSet32) {
      return ((CustomEnumSet32<E>)c).clone();
    } else {
      if (c.isEmpty())
        throw new IllegalArgumentException("Collection is empty");
      Iterator<E> i = c.iterator();
      E first = i.next();
      CustomEnumSet32<E> result = CustomEnumSet32.of(first);
      while (i.hasNext())
        result.add(i.next());
      return result;
    }
  }

  /**
   * Creates an enum set with the same element type as the specified enum
   * set, initially containing all the elements of this type that are
   * <i>not</i> contained in the specified set.
   *
   * @param <E> The class of the elements in the enum set
   * @param s the enum set from whose complement to initialize this enum set
   * @return The complement of the specified set in this set
   * @throws NullPointerException if <tt>s</tt> is null
   */
  public static <E extends Enum<E>> CustomEnumSet32<E> complementOf(CustomEnumSet32<E> s) {
    CustomEnumSet32<E> result = copyOf(s);
    result.complement();
    return result;
  }

  /**
   * Creates an enum set initially containing the specified element.
   *
   * Overloadings of this method exist to initialize an enum set with
   * one through five elements.  A sixth overloading is provided that
   * uses the varargs feature.  This overloading may be used to create
   * an enum set initially containing an arbitrary number of elements, but
   * is likely to run slower than the overloadings that do not use varargs.
   *
   * @param <E> The class of the specified element and of the set
   * @param e the element that this set is to contain initially
   * @throws NullPointerException if <tt>e</tt> is null
   * @return an enum set initially containing the specified element
   */
  public static <E extends Enum<E>> CustomEnumSet32<E> of(E e) {
    CustomEnumSet32<E> result = noneOf(e.getDeclaringClass());
    result.add(e);
    return result;
  }

  /**
   * Creates an enum set initially containing the specified elements.
   *
   * Overloadings of this method exist to initialize an enum set with
   * one through five elements.  A sixth overloading is provided that
   * uses the varargs feature.  This overloading may be used to create
   * an enum set initially containing an arbitrary number of elements, but
   * is likely to run slower than the overloadings that do not use varargs.
   *
   * @param <E> The class of the parameter elements and of the set
   * @param e1 an element that this set is to contain initially
   * @param e2 another element that this set is to contain initially
   * @throws NullPointerException if any parameters are null
   * @return an enum set initially containing the specified elements
   */
  public static <E extends Enum<E>> CustomEnumSet32<E> of(E e1, E e2) {
    CustomEnumSet32<E> result = noneOf(e1.getDeclaringClass());
    result.add(e1);
    result.add(e2);
    return result;
  }

  /**
   * Creates an enum set initially containing the specified elements.
   *
   * Overloadings of this method exist to initialize an enum set with
   * one through five elements.  A sixth overloading is provided that
   * uses the varargs feature.  This overloading may be used to create
   * an enum set initially containing an arbitrary number of elements, but
   * is likely to run slower than the overloadings that do not use varargs.
   *
   * @param <E> The class of the parameter elements and of the set
   * @param e1 an element that this set is to contain initially
   * @param e2 another element that this set is to contain initially
   * @param e3 another element that this set is to contain initially
   * @throws NullPointerException if any parameters are null
   * @return an enum set initially containing the specified elements
   */
  public static <E extends Enum<E>> CustomEnumSet32<E> of(E e1, E e2, E e3) {
    CustomEnumSet32<E> result = noneOf(e1.getDeclaringClass());
    result.add(e1);
    result.add(e2);
    result.add(e3);
    return result;
  }

  /**
   * Creates an enum set initially containing the specified elements.
   *
   * Overloadings of this method exist to initialize an enum set with
   * one through five elements.  A sixth overloading is provided that
   * uses the varargs feature.  This overloading may be used to create
   * an enum set initially containing an arbitrary number of elements, but
   * is likely to run slower than the overloadings that do not use varargs.
   *
   * @param <E> The class of the parameter elements and of the set
   * @param e1 an element that this set is to contain initially
   * @param e2 another element that this set is to contain initially
   * @param e3 another element that this set is to contain initially
   * @param e4 another element that this set is to contain initially
   * @throws NullPointerException if any parameters are null
   * @return an enum set initially containing the specified elements
   */
  public static <E extends Enum<E>> CustomEnumSet32<E> of(E e1, E e2, E e3, E e4) {
    CustomEnumSet32<E> result = noneOf(e1.getDeclaringClass());
    result.add(e1);
    result.add(e2);
    result.add(e3);
    result.add(e4);
    return result;
  }

  /**
   * Creates an enum set initially containing the specified elements.
   *
   * Overloadings of this method exist to initialize an enum set with
   * one through five elements.  A sixth overloading is provided that
   * uses the varargs feature.  This overloading may be used to create
   * an enum set initially containing an arbitrary number of elements, but
   * is likely to run slower than the overloadings that do not use varargs.
   *
   * @param <E> The class of the parameter elements and of the set
   * @param e1 an element that this set is to contain initially
   * @param e2 another element that this set is to contain initially
   * @param e3 another element that this set is to contain initially
   * @param e4 another element that this set is to contain initially
   * @param e5 another element that this set is to contain initially
   * @throws NullPointerException if any parameters are null
   * @return an enum set initially containing the specified elements
   */
  public static <E extends Enum<E>> CustomEnumSet32<E> of(E e1, E e2, E e3, E e4,
                                                  E e5)
  {
    CustomEnumSet32<E> result = noneOf(e1.getDeclaringClass());
    result.add(e1);
    result.add(e2);
    result.add(e3);
    result.add(e4);
    result.add(e5);
    return result;
  }

  /**
   * Creates an enum set initially containing the specified elements.
   * This factory, whose parameter list uses the varargs feature, may
   * be used to create an enum set initially containing an arbitrary
   * number of elements, but it is likely to run slower than the overloadings
   * that do not use varargs.
   *
   * @param <E> The class of the parameter elements and of the set
   * @param first an element that the set is to contain initially
   * @param rest the remaining elements the set is to contain initially
   * @throws NullPointerException if any of the specified elements are null,
   *     or if <tt>rest</tt> is null
   * @return an enum set initially containing the specified elements
   */
  @SafeVarargs
  public static <E extends Enum<E>> CustomEnumSet32<E> of(E first, E... rest) {
    CustomEnumSet32<E> result = noneOf(first.getDeclaringClass());
    result.add(first);
    for (E e : rest)
      result.add(e);
    return result;
  }

  /**
   * Creates an enum set initially containing all of the elements in the
   * range defined by the two specified endpoints.  The returned set will
   * contain the endpoints themselves, which may be identical but must not
   * be out of order.
   *
   * @param <E> The class of the parameter elements and of the set
   * @param from the first element in the range
   * @param to the last element in the range
   * @throws NullPointerException if {@code from} or {@code to} are null
   * @throws IllegalArgumentException if {@code from.compareTo(to) > 0}
   * @return an enum set initially containing all of the elements in the
   *         range defined by the two specified endpoints
   */
  public static <E extends Enum<E>> CustomEnumSet32<E> range(E from, E to) {
    if (from.compareTo(to) > 0)
      throw new IllegalArgumentException(from + " > " + to);
    CustomEnumSet32<E> result = noneOf(from.getDeclaringClass());
    result.addRange(from, to);
    return result;
  }

  /**
   * Returns a copy of this set.
   *
   * @return a copy of this set
   */
  @SuppressWarnings("unchecked")
  public CustomEnumSet32<E> clone() {
    try {
      return (CustomEnumSet32<E>) super.clone(); // TOTEST
    } catch(CloneNotSupportedException e) {
      throw new AssertionError(e);
    }
  }

  /**
   * Throws an exception if e is not of the correct type for this enum set.
   */
  final void typeCheck(E e) {
    Class<?> eClass = e.getClass();
    if (eClass != elementType && eClass.getSuperclass() != elementType)
      throw new ClassCastException(eClass + " != " + elementType);
  }

  /**
   * Returns all of the values comprising E.
   * The result is uncloned, cached, and shared by all callers.
   */
  private static <E extends Enum<E>> E[] getUniverse(Class<E> elementType) {
    return SharedSecrets.getJavaLangAccess()
            .getEnumConstantsShared(elementType);
  }

  /**
   * This class is used to serialize all CustomEnumSet32 instances, regardless of
   * implementation type.  It captures their "logical contents" and they
   * are reconstructed using public static factories.  This is necessary
   * to ensure that the existence of a particular implementation type is
   * an implementation detail.
   *
   * @serial include
   */
  private static class SerializationProxy <E extends Enum<E>>
          implements java.io.Serializable
  {
    /**
     * The element type of this enum set.
     *
     * @serial
     */
    private final Class<E> elementType;

    /**
     * The elements contained in this enum set.
     *
     * @serial
     */
    private final Enum<?>[] elements;

    SerializationProxy(CustomEnumSet32<E> set) {
      elementType = set.elementType;
      elements = set.toArray(ZERO_LENGTH_ENUM_ARRAY);
    }

    // instead of cast to E, we should perhaps use elementType.cast()
    // to avoid injection of forged stream, but it will slow the implementation
    @SuppressWarnings("unchecked")
    private Object readResolve() {
      CustomEnumSet32<E> result = CustomEnumSet32.noneOf(elementType);
      for (Enum<?> e : elements)
        result.add((E)e);
      return result;
    }

    private static final long serialVersionUID = -2444857307148481704L;
  }

  Object writeReplace() {
    return new CustomEnumSet32.SerializationProxy<>(this);
  }

  // readObject method for the serialization proxy pattern
  // See Effective Java, Second Ed., Item 78.
  private void readObject(java.io.ObjectInputStream stream)
          throws java.io.InvalidObjectException {
    throw new java.io.InvalidObjectException("Proxy required");
  }

  private static final long serialVersionUID = -4195798081437821264L;
  /**
   * Bit vector representation of this set.  The 2^k bit indicates the
   * presence of universe[k] in this set.
   */
  private int elements = 0;

  /**
   * Adds the specified range to this enum set, which is empty prior
   * to the call.
   */
  void addRange(E from, E to) {
    elements = (-1 >>>  (from.ordinal() - to.ordinal() - 1)) << from.ordinal();
  }

  /**
   * Adds all of the elements from the appropriate enum type to this enum
   * set, which is empty prior to the call.
   */
  void addAll() {
    if (universe.length != 0)
      elements = -1 >>> -universe.length;
  }

  /**
   * Complements the contents of this enum set.
   */
  void complement() {
    if (universe.length != 0) {
      elements = ~elements;
      elements &= -1 >>> -universe.length;  // Mask unused bits
    }
  }

  /**
   * Returns an iterator over the elements contained in this set.  The
   * iterator traverses the elements in their <i>natural order</i> (which is
   * the order in which the enum constants are declared). The returned
   * Iterator is a "snapshot" iterator that will never throw {@link
   * ConcurrentModificationException}; the elements are traversed as they
   * existed when this call was invoked.
   *
   * @return an iterator over the elements contained in this set
   */
  public Iterator<E> iterator() {
    return new CustomEnumSet32.EnumSetIterator<>();
  }

  private class EnumSetIterator<E extends Enum<E>> implements Iterator<E> {
    /**
     * A bit vector representing the elements in the set not yet
     * returned by this iterator.
     */
    int unseen;

    /**
     * The bit representing the last element returned by this iterator
     * but not removed, or zero if no such element exists.
     */
    int lastReturned = 0;

    EnumSetIterator() {
      unseen = elements;
    }

    public boolean hasNext() {
      return unseen != 0;
    }

    @SuppressWarnings("unchecked")
    public E next() {
      if (unseen == 0)
        throw new NoSuchElementException();
      lastReturned = unseen & -unseen;
      unseen -= lastReturned;
      return (E) universe[Integer.numberOfTrailingZeros(lastReturned)];
    }

    public void remove() {
      if (lastReturned == 0)
        throw new IllegalStateException();
      elements &= ~lastReturned;
      lastReturned = 0;
    }

    @Override
    public void forEachRemaining(Consumer<? super E> action) {
      each(unseen, action);
      unseen = 0;
      lastReturned = 0;
    }
  }

  /**
   * Returns the number of elements in this set.
   *
   * @return the number of elements in this set
   */
  public int size() {
    return Integer.bitCount(elements);
  }

  /**
   * Returns <tt>true</tt> if this set contains no elements.
   *
   * @return <tt>true</tt> if this set contains no elements
   */
  public boolean isEmpty() {
    return elements == 0;
  }

  /**
   * Returns <tt>true</tt> if this set contains the specified element.
   *
   * @param e element to be checked for containment in this collection
   * @return <tt>true</tt> if this set contains the specified element
   */
  public boolean contains(Object e) {
    if (e == null)
      return false;
    Class<?> eClass = e.getClass();
    if (eClass != elementType && eClass.getSuperclass() != elementType)
      return false;

    return (elements & (1 << ((Enum<?>)e).ordinal())) != 0;
  }

  // Modification Operations

  /**
   * Adds the specified element to this set if it is not already present.
   *
   * @param e element to be added to this set
   * @return <tt>true</tt> if the set changed as a result of the call
   *
   * @throws NullPointerException if <tt>e</tt> is null
   */
  public boolean add(E e) {
    typeCheck(e);

    int oldElements = elements;
    elements |= (1 << ((Enum<?>)e).ordinal());
    return elements != oldElements;
  }

  /**
   * Removes the specified element from this set if it is present.
   *
   * @param e element to be removed from this set, if present
   * @return <tt>true</tt> if the set contained the specified element
   */
  public boolean remove(Object e) {
    if (e == null)
      return false;
    Class<?> eClass = e.getClass();
    if (eClass != elementType && eClass.getSuperclass() != elementType)
      return false;

    int oldElements = elements;
    elements &= ~(1 << ((Enum<?>)e).ordinal());
    return elements != oldElements;
  }

  // Bulk Operations

  /**
   * Returns <tt>true</tt> if this set contains all of the elements
   * in the specified collection.
   *
   * @param c collection to be checked for containment in this set
   * @return <tt>true</tt> if this set contains all of the elements
   *        in the specified collection
   * @throws NullPointerException if the specified collection is null
   */
  public boolean containsAll(Collection<?> c) {
    if (!(c instanceof CustomEnumSet32)) {
      for (Object e : c)
        if (!contains(e))
          return false;
      return true;
    };

    CustomEnumSet32<?> es = (CustomEnumSet32<?>)c;
    if (es.elementType != elementType)
      return es.isEmpty();

    return (es.elements & ~elements) == 0;
  }

  /**
   * Adds all of the elements in the specified collection to this set.
   *
   * @param c collection whose elements are to be added to this set
   * @return <tt>true</tt> if this set changed as a result of the call
   * @throws NullPointerException if the specified collection or any
   *     of its elements are null
   */
  public boolean addAll(Collection<? extends E> c) {
    if (!(c instanceof CustomEnumSet32)) {
      boolean modified = false;
      for (E e : c)
        if (add(e))
          modified = true;
      return modified;

    }

    CustomEnumSet32<?> es = (CustomEnumSet32<?>)c;
    if (es.elementType != elementType) {
      if (es.isEmpty())
        return false;
      else
        throw new ClassCastException(
                es.elementType + " != " + elementType);
    }

    int oldElements = elements;
    elements |= es.elements;
    return elements != oldElements;
  }

  /**
   * Removes from this set all of its elements that are contained in
   * the specified collection.
   *
   * @param c elements to be removed from this set
   * @return <tt>true</tt> if this set changed as a result of the call
   * @throws NullPointerException if the specified collection is null
   */
  public boolean removeAll(Collection<?> c) {
    if (!(c instanceof CustomEnumSet32)) {
      Objects.requireNonNull(c);
      boolean modified = false;

      if (size() > c.size()) {
        for (Iterator<?> i = c.iterator(); i.hasNext(); )
          modified |= remove(i.next());
      } else {
        for (Iterator<?> i = iterator(); i.hasNext(); ) {
          if (c.contains(i.next())) {
            i.remove();
            modified = true;
          }
        }
      }
      return modified;
    }

    CustomEnumSet32<?> es = (CustomEnumSet32<?>)c;
    if (es.elementType != elementType)
      return false;

    int oldElements = elements;
    elements &= ~es.elements;
    return elements != oldElements;
  }

  /**
   * Retains only the elements in this set that are contained in the
   * specified collection.
   *
   * @param c elements to be retained in this set
   * @return <tt>true</tt> if this set changed as a result of the call
   * @throws NullPointerException if the specified collection is null
   */
  public boolean retainAll(Collection<?> c) {
    if (!(c instanceof CustomEnumSet32)) {
      Objects.requireNonNull(c);
      boolean modified = false;
      Iterator<E> it = iterator();
      while (it.hasNext()) {
        if (!c.contains(it.next())) {
          it.remove();
          modified = true;
        }
      }
      return modified;
    }

    CustomEnumSet32<?> es = (CustomEnumSet32<?>)c;
    if (es.elementType != elementType) {
      boolean changed = (elements != 0);
      elements = 0;
      return changed;
    }

    int oldElements = elements;
    elements &= es.elements;
    return elements != oldElements;
  }

  /**
   * Removes all of the elements from this set.
   */
  public void clear() {
    elements = 0;
  }

  /**
   * Compares the specified object with this set for equality.  Returns
   * <tt>true</tt> if the given object is also a set, the two sets have
   * the same size, and every member of the given set is contained in
   * this set.
   *
   * @param o object to be compared for equality with this set
   * @return <tt>true</tt> if the specified object is equal to this set
   */
  public boolean equals(Object o) {
    if (!(o instanceof CustomEnumSet32)) {
      if (!(o instanceof Set))
        return false;
      Collection<?> c = (Collection<?>) o;
      if (c.size() != size())
        return false;
      try {
        return containsAll(c);
      } catch (ClassCastException unused)   {
        return false;
      } catch (NullPointerException unused) {
        return false;
      }

    }

    CustomEnumSet32<?> es = (CustomEnumSet32<?>)o;
    if (es.elementType != elementType)
      return elements == 0 && es.elements == 0;
    return es.elements == elements;
  }

  @Override
  public void forEach(Consumer<? super E> action) {
    each(elements, action);
  }

  @SuppressWarnings("unchecked")
  E first(int es) {
    return (E) universe[Integer.numberOfTrailingZeros(es)];
  }

  int rest(int es) {
    return es - Integer.lowestOneBit(es);
  }

  void each(int es, Consumer<? super E> action) {
    if (es == 0) {
      Objects.requireNonNull(action);
      return;
    }
    do {
      action.accept(first(es));
      es = rest(es);
    } while (es != 0);
  }

  @Override
  public Spliterator<E> spliterator() {
    return new EnumSetSpliterator(elements);
  }

  private final class EnumSetSpliterator implements Spliterator<E> {
    private int unseen;

    EnumSetSpliterator(int es) {
      unseen = es;
    }

    @Override
    public int characteristics() {
      return Spliterator.ORDERED |
              Spliterator.SORTED |
              Spliterator.NONNULL |
              Spliterator.DISTINCT |
              Spliterator.SIZED |
              Spliterator.SUBSIZED;
    }

    @Override
    public long estimateSize() {
      return Integer.bitCount(unseen);
    }

    @Override
    public void forEachRemaining(Consumer<? super E> action) {
      each(unseen, action);
      unseen = 0;
    }

    @Override
    public Comparator<? super E> getComparator() {
      return null;
    }

    @Override
    public boolean tryAdvance(Consumer<? super E> action) {
      int es = unseen;
      if (es == 0) {
        Objects.requireNonNull(action);
        return false;
      }
      action.accept(first(es));
      unseen = rest(es);
      return true;
    }

    @Override
    public Spliterator<E> trySplit() {
      int es = unseen;
      if (es == 0)
        return null;

      int lo = Integer.numberOfTrailingZeros(es);
      int hi = Integer.SIZE - Integer.numberOfLeadingZeros(es);
      if (lo == hi - 1)
        return null;

      int mid = (lo + hi) >>> 1;
      int headSet = es & ((1 << mid) - 1);
      unseen = es & ~headSet;
      return new EnumSetSpliterator(headSet);
    }
  }

  /**
   * {@inheritDoc}
   *
   * <p>This implementation returns an array containing all the elements
   * returned by this collection's iterator, in the same order, stored in
   * consecutive elements of the array, starting with index {@code 0}.
   * The length of the returned array is equal to the number of elements
   * returned by the iterator, even if the size of this collection changes
   * during iteration, as might happen if the collection permits
   * concurrent modification during iteration.  The {@code size} method is
   * called only as an optimization hint; the correct result is returned
   * even if the iterator returns a different number of elements.
   *
   * <p>This method is equivalent to:
   *
   *  <pre> {@code
   * List<E> list = new ArrayList<E>(size());
   * for (E e : this)
   *     list.add(e);
   * return list.toArray();
   * }</pre>
   */
  public Object[] toArray() {
    // Estimate size of array; be prepared to see more or fewer elements
    Object[] r = new Object[size()];
    Iterator<E> it = iterator();
    for (int i = 0; i < r.length; i++) {
      if (! it.hasNext()) // fewer elements than expected
        return Arrays.copyOf(r, i);
      r[i] = it.next();
    }
    return it.hasNext() ? finishToArray(r, it) : r;
  }

  /**
   * {@inheritDoc}
   *
   * <p>This implementation returns an array containing all the elements
   * returned by this collection's iterator in the same order, stored in
   * consecutive elements of the array, starting with index {@code 0}.
   * If the number of elements returned by the iterator is too large to
   * fit into the specified array, then the elements are returned in a
   * newly allocated array with length equal to the number of elements
   * returned by the iterator, even if the size of this collection
   * changes during iteration, as might happen if the collection permits
   * concurrent modification during iteration.  The {@code size} method is
   * called only as an optimization hint; the correct result is returned
   * even if the iterator returns a different number of elements.
   *
   * <p>This method is equivalent to:
   *
   *  <pre> {@code
   * List<E> list = new ArrayList<E>(size());
   * for (E e : this)
   *     list.add(e);
   * return list.toArray(a);
   * }</pre>
   *
   * @throws ArrayStoreException  {@inheritDoc}
   * @throws NullPointerException {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  public <T> T[] toArray(T[] a) {
    // Estimate size of array; be prepared to see more or fewer elements
    int size = size();
    T[] r = a.length >= size ? a :
            (T[])java.lang.reflect.Array
                    .newInstance(a.getClass().getComponentType(), size);
    Iterator<E> it = iterator();

    for (int i = 0; i < r.length; i++) {
      if (! it.hasNext()) { // fewer elements than expected
        if (a == r) {
          r[i] = null; // null-terminate
        } else if (a.length < i) {
          return Arrays.copyOf(r, i);
        } else {
          System.arraycopy(r, 0, a, 0, i);
          if (a.length > i) {
            a[i] = null;
          }
        }
        return a;
      }
      r[i] = (T)it.next();
    }
    // more elements than expected
    return it.hasNext() ? finishToArray(r, it) : r;
  }

  /**
   * The maximum size of array to allocate.
   * Some VMs reserve some header words in an array.
   * Attempts to allocate larger arrays may result in
   * OutOfMemoryError: Requested array size exceeds VM limit
   */
  private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

  /**
   * Reallocates the array being used within toArray when the iterator
   * returned more elements than expected, and finishes filling it from
   * the iterator.
   *
   * @param r the array, replete with previously stored elements
   * @param it the in-progress iterator over this collection
   * @return array containing the elements in the given array, plus any
   *         further elements returned by the iterator, trimmed to size
   */
  @SuppressWarnings("unchecked")
  private static <T> T[] finishToArray(T[] r, Iterator<?> it) {
    int i = r.length;
    while (it.hasNext()) {
      int cap = r.length;
      if (i == cap) {
        int newCap = cap + (cap >> 1) + 1;
        // overflow-conscious code
        if (newCap - MAX_ARRAY_SIZE > 0)
          newCap = hugeCapacity(cap + 1);
        r = Arrays.copyOf(r, newCap);
      }
      r[i++] = (T)it.next();
    }
    // trim if overallocated
    return (i == r.length) ? r : Arrays.copyOf(r, i);
  }

  private static int hugeCapacity(int minCapacity) {
    if (minCapacity < 0) // overflow
      throw new OutOfMemoryError
              ("Required array size too large");
    return (minCapacity > MAX_ARRAY_SIZE) ?
            Integer.MAX_VALUE :
            MAX_ARRAY_SIZE;
  }
}
