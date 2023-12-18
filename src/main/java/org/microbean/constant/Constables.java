/* -*- mode: Java; c-basic-offset: 2; indent-tabs-mode: nil; coding: utf-8-unix -*-
 *
 * Copyright © 2022–2023 microBean™.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.microbean.constant;

import java.lang.constant.ClassDesc;
import java.lang.constant.Constable;
import java.lang.constant.ConstantDesc;
import java.lang.constant.DirectMethodHandleDesc;
import java.lang.constant.DynamicConstantDesc;
import java.lang.constant.MethodHandleDesc;
import java.lang.constant.MethodTypeDesc;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

import java.util.function.Function;

import static java.lang.constant.ConstantDescs.BSM_INVOKE;
import static java.lang.constant.ConstantDescs.CD_Collection;
import static java.lang.constant.ConstantDescs.CD_List;
import static java.lang.constant.ConstantDescs.CD_Map;
import static java.lang.constant.ConstantDescs.CD_Object;
import static java.lang.constant.ConstantDescs.CD_Set;
import static java.lang.constant.ConstantDescs.NULL;

import static org.microbean.constant.ConstantDescs.CD_Arrays;
import static org.microbean.constant.ConstantDescs.CD_Collections;
import static org.microbean.constant.ConstantDescs.CD_Comparator;
import static org.microbean.constant.ConstantDescs.CD_Entry;
import static org.microbean.constant.ConstantDescs.CD_HashSet;
import static org.microbean.constant.ConstantDescs.CD_Optional;
import static org.microbean.constant.ConstantDescs.CD_SimpleImmutableEntry;
import static org.microbean.constant.ConstantDescs.CD_SortedMap;
import static org.microbean.constant.ConstantDescs.CD_SortedSet;

/**
 * A utility class containing {@code static} methods that can describe various things in {@link Constable} ways.
 *
 * @author <a href="https://about.me/lairdnelson" target="_parent">Laird Nelson</a>
 *
 * @see #describeConstable(Collection)
 *
 * @see #describeConstable(Map)
 *
 * @see #describeConstable(Entry)
 *
 * @see Constable
 */
public final class Constables {


  private static final ClassDesc CD_BootstrapMethods = ClassDesc.of("org.microbean.invoke.BootstrapMethods");

  private static final ConstantDesc[] EMPTY_CONSTANTDESC_ARRAY = new ConstantDesc[0];


  /*
   * Constructors.
   */


  private Constables() {
    super();
  }


  /*
   * Static methods.
   */


  @SuppressWarnings("unchecked")
  public static final Optional<? extends ConstantDesc> describeConstable(final Object o) {
    return
      o == null ? Optional.of(NULL) :
      o instanceof Constable c ? c.describeConstable() :
      o instanceof ConstantDesc cd ? Optional.of(cd) :
      o instanceof List<?> l ? describeConstable(l) :
      o instanceof Set<?> s ? describeConstable(s) :
      o instanceof Map<?, ?> m ? describeConstable(m) :
      o instanceof Entry<?, ?> e ? describeConstable(e) :
      o instanceof Optional<?> opt ? describeConstable(opt) :
      Optional.empty();
  }

  // Note that this describles the Optional itself, i.e. this is not a convenient shortcut to get to the optional's payload
  public static final Optional<? extends ConstantDesc> describeConstable(final Optional<?> o) {
    return describeConstable(o, Constables::describeConstable);
  }

  public static final <T> Optional<? extends ConstantDesc> describeConstable(final Optional<? extends T> o,
                                                                             final Function<? super T, ? extends Optional<? extends ConstantDesc>> f) {
    if (o == null) {
      return Optional.of(NULL);
    } else if (o.isEmpty()) {
      return
        Optional.of(callStatic(CD_Optional,
                               "empty",
                               MethodTypeDesc.of(CD_Optional)));
    }
    final Optional<? extends ConstantDesc> payload = f == null ? describeConstable(o.orElseThrow()) : f.apply(o.orElseThrow());
    if (payload.isEmpty()) {
      return Optional.empty();
    }
    return
      Optional.of(callStatic(CD_Optional,
                             "ofNullable",
                             MethodTypeDesc.of(CD_Optional, CD_Object),
                             payload.orElseThrow()));
  }

  public static final Optional<? extends ConstantDesc> describeConstable(final ConstantDesc cd) {
    return
      cd == null ? Optional.of(NULL) :
      cd instanceof Constable c ? c.describeConstable() : // if something implements Constable, always give it a chance to modify its own description
      Optional.of(cd);
  }

  public static final Optional<? extends ConstantDesc> describeConstable(final Constable c) {
    return
      c == null ? Optional.of(NULL) :
      c.describeConstable();
  }

  public static final Optional<? extends ConstantDesc> describeConstable(final Collection<?> elements) {
    return describeConstable(elements, Constables::empty, Constables::describeConstable);
  }

  public static final <E> Optional<? extends ConstantDesc> describeConstable(final Collection<? extends E> elements,
                                                                             final Function<? super E, ? extends Optional<? extends ConstantDesc>> f) {
    return describeConstable(elements, Constables::empty, f);
  }

  public static final <E> Optional<? extends ConstantDesc>
    describeConstable(final Collection<? extends E> elements,
                      final Function<? super Comparator<?>, ? extends Optional<? extends ConstantDesc>> cf,
                      final Function<? super E, ? extends Optional<? extends ConstantDesc>> f) {
    return
      elements == null ? Optional.of(NULL) :
      elements instanceof List<? extends E> l ? describeConstable0(l, CD_List, cf, f) :
      elements instanceof Set<? extends E> s ? describeConstable0(s, CD_Set, cf, f) :
      Optional.empty();
  }

  public static final Optional<? extends ConstantDesc> describeConstable(final List<?> elements) {
    return describeConstable0(elements, CD_List, Constables::empty, Constables::describeConstable);
  }

  public static final <E> Optional<? extends ConstantDesc> describeConstable(final List<? extends E> elements,
                                                                             final Function<? super E, ? extends Optional<? extends ConstantDesc>> f) {
    return describeConstable0(elements, CD_List, Constables::empty, f);
  }

  public static final Optional<? extends ConstantDesc> describeConstable(final Set<?> elements) {
    return describeConstable0(elements, CD_Set, Constables::empty, Constables::describeConstable);
  }

  public static final <E> Optional<? extends ConstantDesc> describeConstable(final Set<? extends E> elements,
                                                                             final Function<? super E, ? extends Optional<? extends ConstantDesc>> f) {
    return describeConstable0(elements, CD_Set, Constables::empty, f);
  }

  public static final <E> Optional<? extends ConstantDesc>
    describeConstable(final Set<? extends E> elements,
                      final Function<? super Comparator<?>, ? extends Optional<? extends ConstantDesc>> cf,
                      final Function<? super E, ? extends Optional<? extends ConstantDesc>> f) {
    return describeConstable0(elements, CD_Set, cf, f);
  }

  public static final Optional<? extends ConstantDesc> describeConstable(final Map<?, ?> map) {
    return describeConstable0(map, Constables::empty, Constables::describeConstable, Constables::describeConstable);
  }

  public static final <K, V> Optional<? extends ConstantDesc> describeConstable(final Map<? extends K, ? extends V> map,
                                                                                final Function<? super K, ? extends Optional<? extends ConstantDesc>> kf,
                                                                                final Function<? super V, ? extends Optional<? extends ConstantDesc>> vf) {
    return describeConstable0(map, Constables::empty, kf, vf);
  }

  public static final <K, V> Optional<? extends ConstantDesc>
    describeConstable(final Map<? extends K, ? extends V> map,
                      final Function<? super Comparator<?>, ? extends Optional<? extends ConstantDesc>> cf,
                      final Function<? super K, ? extends Optional<? extends ConstantDesc>> kf,
                      final Function<? super V, ? extends Optional<? extends ConstantDesc>> vf) {
    return describeConstable0(map, cf, kf, vf);
  }

  public static final Optional<? extends ConstantDesc> describeConstable(final Entry<?, ?> entry) {
    return describeConstable(entry, Constables::describeConstable, Constables::describeConstable);
  }

  public static final <K, V> Optional<? extends ConstantDesc> describeConstable(final Entry<? extends K, ? extends V> entry,
                                                                                final Function<? super K, ? extends Optional<? extends ConstantDesc>> kf,
                                                                                final Function<? super V, ? extends Optional<? extends ConstantDesc>> vf) {
    return
      entry == null ? Optional.of(NULL) :
      entry instanceof Constable c ? c.describeConstable() :
      describeConstable(entry.getKey(), entry.getValue(), kf, vf);
  }

  public static final <K, V> Optional<? extends ConstantDesc>
    describeConstable(final K k,
                      final V v,
                      final Function<? super K, ? extends Optional<? extends ConstantDesc>> kf,
                      final Function<? super V, ? extends Optional<? extends ConstantDesc>> vf) {
    final Optional<? extends ConstantDesc> key =
      k instanceof Constable c ? c.describeConstable() : kf == null ? describeConstable(k) : kf.apply(k);
    if (key.isPresent()) {
      final Optional<? extends ConstantDesc> value =
        v instanceof Constable c ? c.describeConstable() : vf == null ? describeConstable(v) : vf.apply(v);
      if (value.isPresent()) {
        final ConstantDesc keyDesc = key.orElseThrow();
        final ConstantDesc valueDesc = value.orElseThrow();
        if (keyDesc == NULL || valueDesc == NULL) {
          return
            Optional.of(construct(CD_SimpleImmutableEntry,
                                  new ClassDesc[] { CD_Object, CD_Object },// K and V erasures
                                  keyDesc,
                                  valueDesc));
        }
        // Map#entry(K, V)
        return
          Optional.of(callInterfaceStatic(CD_Map,
                                          "entry",
                                          MethodTypeDesc.of(CD_Entry,
                                                            CD_Object, // K erasure
                                                            CD_Object), // V erasure
                                          keyDesc,
                                          valueDesc));
      }
    }
    return Optional.empty();
  }


  /*
   * Private static methods.
   */


  private static final <E> Optional<? extends ConstantDesc>
    describeConstable0(final SortedSet<? extends E> set,
                       final Function<? super Comparator<?>, ? extends Optional<? extends ConstantDesc>> cf,
                       final Function<? super E, ? extends Optional<? extends ConstantDesc>> f) {
    if (set == null) {
      return Optional.of(NULL);
    } else if (set instanceof Constable c) {
      return c.describeConstable();
    }

    // If the set has a user-supplied Comparator, we need to describe it as a ConstantDesc too.
    final ConstantDesc comparatorDesc = describeComparator(set.comparator(), cf);
    if (comparatorDesc == null) {
      return Optional.empty();
    }

    if (set.isEmpty()) {
      if (comparatorDesc == NULL) {
        return Optional.of(callStatic(CD_Collections, "emptySortedSet", MethodTypeDesc.of(CD_SortedSet)));
      }
      return
        Optional.of(callStatic(CD_BootstrapMethods,
                               "immutableSortedSetOf",
                               MethodTypeDesc.of(CD_SortedSet, CD_Comparator),
                               comparatorDesc));
    }

    final ConstantDesc[] args = elements(set, f);
    if (args.length <= 0) {
      return Optional.empty();
    }

    final ConstantDesc unsortedListDesc = asList(args);

    if (comparatorDesc == NULL) {
      return
        Optional.of(callStatic(CD_BootstrapMethods,
                               "immutableSortedSetOf",
                               MethodTypeDesc.of(CD_SortedSet, CD_Collection),
                               unsortedListDesc));
    }
    return
      Optional.of(callStatic(CD_BootstrapMethods,
                             "immutableSortedSetOf",
                             MethodTypeDesc.of(CD_SortedSet, CD_Collection, CD_Comparator),
                             unsortedListDesc,
                             comparatorDesc));
  }

  private static final <E> Optional<? extends ConstantDesc>
    describeConstable0(final Collection<? extends E> elements,
                       final ClassDesc listOrSetClassDesc,
                       final Function<? super Comparator<?>, ? extends Optional<? extends ConstantDesc>> cf,
                       Function<? super E, ? extends Optional<? extends ConstantDesc>> f) {
    assert CD_List.equals(listOrSetClassDesc) || CD_Set.equals(listOrSetClassDesc) : String.valueOf(listOrSetClassDesc);
    if (elements == null) {
      return Optional.of(NULL);
    } else if (elements instanceof Constable c) {
      return c.describeConstable();
    } else if (elements instanceof SortedSet<? extends E> ss) {
      return describeConstable0(ss, cf, f);
    } else if (elements.isEmpty()) {
      return Optional.of(callInterfaceStatic(listOrSetClassDesc, "of", listOrSetClassDesc));
    }

    if (f == null) {
      f = Constables::describeConstable;
    }
    final int elementsSize = elements.size();
    final ConstantDesc[] args = new ConstantDesc[elementsSize];
    boolean nulls = false;
    int i = 0;
    for (final E element : elements) {
      final Optional<? extends ConstantDesc> arg = element instanceof Constable c ? c.describeConstable() : f.apply(element);
      if (arg == null || arg.isEmpty()) {
        // If there's even one thing that cannot be described, then the whole thing cannot be described.
        return Optional.empty();
      }
      if (element == null) {
        if (!nulls) {
          nulls = true;
        }
      }
      args[i++] = arg.orElseThrow();
    }
    if (nulls) {
      final ConstantDesc cd = asList(args);
      if (CD_List.equals(listOrSetClassDesc)) {
        assert elements instanceof List;
        return
          Optional.of(callStatic(CD_Collections,
                                 "unmodifiableList",
                                 MethodTypeDesc.of(CD_List, CD_List),
                                 cd));
      }
      assert elements instanceof Set;
      return
        Optional.of(callStatic(CD_Collections,
                               "unmodifiableSet",
                               MethodTypeDesc.of(CD_Set, CD_Set),
                               construct(CD_HashSet, new ClassDesc[] { CD_Collection }, cd)));
    }
    final MethodTypeDesc ofMethodTypeDesc;
    if (elementsSize <= 10) {
      // List.of() and Set.of() have explicit polymorphic overrides for parameter counts of up to 10.
      final ClassDesc[] parameterArray = new ClassDesc[elementsSize];
      Arrays.fill(parameterArray, CD_Object); // Object is the erasure of E
      ofMethodTypeDesc = MethodTypeDesc.of(listOrSetClassDesc, parameterArray);
    } else {
      // After 10 parameters, List.of() and Set.of() fall back on varargs.
      ofMethodTypeDesc = MethodTypeDesc.of(listOrSetClassDesc, CD_Object.arrayType());
    }
    return
      Optional.of(callInterfaceStatic(listOrSetClassDesc,
                                      "of",
                                      ofMethodTypeDesc,
                                      args));
  }

  private static final <K, V> Optional<? extends ConstantDesc>
    describeConstable0(final SortedMap<? extends K, ? extends V> map,
                       final Function<? super Comparator<?>, ? extends Optional<? extends ConstantDesc>> cf,
                       final Function<? super K, ? extends Optional<? extends ConstantDesc>> kf,
                       final Function<? super V, ? extends Optional<? extends ConstantDesc>> vf) {
    if (map == null) {
      return Optional.of(NULL);
    } else if (map instanceof Constable c) {
      return c.describeConstable();
    }

    // If the map has a user-supplied Comparator, we need to describe it as a ConstantDesc too.
    final ConstantDesc comparatorDesc = describeComparator(map.comparator(), cf);
    if (comparatorDesc == null) {
      return Optional.empty();
    }

    if (map.isEmpty()) {
      if (comparatorDesc == NULL) {
        return Optional.of(callStatic(CD_Collections, "emptySortedMap", MethodTypeDesc.of(CD_SortedMap)));
      }
      return
        Optional.of(callStatic(CD_BootstrapMethods,
                               "immutableEmptySortedMap",
                               MethodTypeDesc.of(CD_SortedMap, CD_Comparator),
                               comparatorDesc));
    }

    final ConstantDesc entriesListDesc = asList(entries(map, kf, vf, false));

    if (comparatorDesc == NULL) {
      return
        Optional.of(callStatic(CD_BootstrapMethods,
                               "immutableSortedMapOf",
                               MethodTypeDesc.of(CD_SortedMap, CD_Collection),
                               entriesListDesc));
    }
    return
      Optional.of(callStatic(CD_BootstrapMethods,
                             "immutableSortedMapOf",
                             MethodTypeDesc.of(CD_SortedMap, CD_Collection, CD_Comparator),
                             entriesListDesc,
                             comparatorDesc));
  }

  private static final <K, V> Optional<? extends ConstantDesc>
    describeConstable0(final Map<? extends K, ? extends V> map,
                       final Function<? super Comparator<?>, ? extends Optional<? extends ConstantDesc>> cf,
                       final Function<? super K, ? extends Optional<? extends ConstantDesc>> kf,
                       final Function<? super V, ? extends Optional<? extends ConstantDesc>> vf) {
    if (map == null) {
      return Optional.of(NULL);
    } else if (map instanceof Constable c) {
      return c.describeConstable();
    } else if (map instanceof SortedMap<? extends K, ? extends V> sm) {
      return describeConstable0(sm, cf, kf, vf);
    } else if (map.isEmpty()) {
      // Map.of()
      return Optional.of(callInterfaceStatic(CD_Map, "of", CD_Map));
    }

    final ConstantDesc[] args = entries(map, kf, vf, true);
    if (args.length <= 0) {
      return Optional.empty();
    }

    // Map.ofEntries(Map.Entry...)
    return Optional.of(callInterfaceStatic(CD_Map, "ofEntries", MethodTypeDesc.of(CD_Map, CD_Entry.arrayType()), args));
  }

  private static final <E> ConstantDesc[] elements(final Collection<? extends E> source,
                                                   Function<? super E, ? extends Optional<? extends ConstantDesc>> f) {
    if (f == null) {
      f = Constables::describeConstable;
    }
    final ConstantDesc[] args = new ConstantDesc[source.size()];
    int i = 0;
    for (final E element : source) {
      final Optional<? extends ConstantDesc> arg = element instanceof Constable c ? c.describeConstable() : f.apply(element);
      if (arg == null || arg.isEmpty()) {
        // If there's even one thing that cannot be described, then the whole thing cannot be described.
        return EMPTY_CONSTANTDESC_ARRAY;
      }
      args[i++] = arg.orElseThrow();
    }
    return args;
  }

  private static final <K, V> ConstantDesc[] entries(final Map<? extends K, ? extends V> map,
                                                     final Function<? super K, ? extends Optional<? extends ConstantDesc>> kf,
                                                     final Function<? super V, ? extends Optional<? extends ConstantDesc>> vf,
                                                     final boolean rejectNulls) {
    if (map.isEmpty()) {
      return EMPTY_CONSTANTDESC_ARRAY;
    }
    final ConstantDesc[] args = new ConstantDesc[map.size()];
    int i = 0;
    for (final Entry<? extends K, ? extends V> entry : map.entrySet()) {
      if (rejectNulls && (entry.getKey() == null || entry.getValue() == null)) {
        return EMPTY_CONSTANTDESC_ARRAY;
      }
      final Optional<? extends ConstantDesc> e = describeConstable(entry, kf, vf);
      if (e.isEmpty()) {
        // If there's even one thing that cannot be described, then the whole thing can't be described.
        return EMPTY_CONSTANTDESC_ARRAY;
      }
      args[i++] = e.orElseThrow();
    }
    return args;
  }

  private static final DynamicConstantDesc<?> construct(final ClassDesc cd, final ClassDesc[] constructorParameterTypes, final ConstantDesc... args) {
    final ConstantDesc[] newArgs = new ConstantDesc[args == null || args.length <= 0 ? 1 : args.length + 1];
    newArgs[0] = MethodHandleDesc.ofConstructor(cd, constructorParameterTypes);
    if (newArgs.length > 1) {
      System.arraycopy(args, 0, newArgs, 1, args.length);
    }
    return DynamicConstantDesc.of(BSM_INVOKE, newArgs);
  }

  private static final DynamicConstantDesc<?> callInterfaceStatic(final ClassDesc cd, final String name, final ClassDesc returnType) {
    return callInterfaceStatic(cd, name, MethodTypeDesc.of(returnType));
  }

  private static final DynamicConstantDesc<?> callInterfaceStatic(final ClassDesc cd, final String name, final MethodTypeDesc sig, final ConstantDesc... args) {
    return call(DirectMethodHandleDesc.Kind.INTERFACE_STATIC, cd, name, sig, args);
  }

  private static final DynamicConstantDesc<?> callStatic(final ClassDesc cd, final String name, final MethodTypeDesc sig, final ConstantDesc... args) {
    return call(DirectMethodHandleDesc.Kind.STATIC, cd, name, sig, args);
  }

  private static final DynamicConstantDesc<?> call(final DirectMethodHandleDesc.Kind kind,
                                                   final ClassDesc cd,
                                                   final String name,
                                                   final MethodTypeDesc sig,
                                                   final ConstantDesc... args) {
    final ConstantDesc[] newArgs = new ConstantDesc[args == null || args.length == 0 ? 1 : args.length + 1];
    newArgs[0] = MethodHandleDesc.ofMethod(kind, cd, name, sig);
    if (newArgs.length > 1) {
      System.arraycopy(args, 0, newArgs, 1, args.length);
    }
    return DynamicConstantDesc.of(BSM_INVOKE, newArgs);
  }

  private static final <T> Optional<? extends ConstantDesc> empty(final T ignored) {
    return Optional.empty();
  }

  private static final ConstantDesc describeComparator(final Comparator<?> comparator,
                                                       final Function<? super Comparator<?>, ? extends Optional<? extends ConstantDesc>> cf) {
    return
      comparator == null ? NULL :
      comparator instanceof Constable c ? c.describeConstable().orElse(null) :
      cf == null ? null :
      cf.apply(comparator).orElse(null);
  }

  private static final DynamicConstantDesc<?> asList(final ConstantDesc[] args) {
    return callStatic(CD_Arrays, "asList", MethodTypeDesc.of(CD_List, CD_Object.arrayType()), args);
  }

}
