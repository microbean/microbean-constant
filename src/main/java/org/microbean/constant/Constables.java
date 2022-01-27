/* -*- mode: Java; c-basic-offset: 2; indent-tabs-mode: nil; coding: utf-8-unix -*-
 *
 * Copyright © 2022 microBean™.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
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

import static org.microbean.constant.ConstantDescs.CD_Collections;
import static org.microbean.constant.ConstantDescs.CD_Comparator;
import static org.microbean.constant.ConstantDescs.CD_Entry;
import static org.microbean.constant.ConstantDescs.CD_SortedMap;
import static org.microbean.constant.ConstantDescs.CD_SortedSet;
import static org.microbean.constant.ConstantDescs.CD_TreeMap;
import static org.microbean.constant.ConstantDescs.CD_TreeSet;

import static org.microbean.invoke.ConstantDescs.CD_BootstrapMethods;

public final class Constables {

  private Constables() {
    super();
  }

  public static final Optional<? extends ConstantDesc> describeConstable(final ConstantDesc cd) {
    if (cd == null) {
      return Optional.of(NULL);
    } else if (cd instanceof Constable c) {
      return c.describeConstable();
    } else {
      return Optional.of(cd);
    }
  }

  public static final Optional<? extends ConstantDesc> describeConstable(final Constable c) {
    return c == null ? Optional.of(NULL) : c.describeConstable();
  }

  public static final <E> Optional<? extends ConstantDesc> describeConstable(final Collection<? extends E> elements) {
    return describeConstable(elements, Constables::empty, Constables::empty);
  }

  public static final <E> Optional<? extends ConstantDesc> describeConstable(final Collection<? extends E> elements,
                                                                             final Function<? super E, ? extends Optional<? extends ConstantDesc>> f) {
    return describeConstable(elements, Constables::empty, f);
  }

  public static final <E> Optional<? extends ConstantDesc> describeConstable(final Collection<? extends E> elements,
                                                                             final Function<? super Comparator<?>, ? extends Optional<? extends ConstantDesc>> cf,
                                                                             final Function<? super E, ? extends Optional<? extends ConstantDesc>> f) {
    if (elements == null) {
      return Optional.of(NULL);
    } else if (elements instanceof List<? extends E> l) {
      return describeConstable(l, CD_List, cf, f);
    } else if (elements instanceof Set<? extends E> s) {
      return describeConstable(s, CD_Set, cf, f);
    } else {
      return Optional.empty();
    }
  }

  public static final <E> Optional<? extends ConstantDesc> describeConstable(final List<? extends E> elements) {
    return describeConstable(elements, CD_List, Constables::empty, Constables::empty);
  }

  public static final <E> Optional<? extends ConstantDesc> describeConstable(final List<? extends E> elements,
                                                                             final Function<? super E, ? extends Optional<? extends ConstantDesc>> f) {
    return describeConstable(elements, CD_List, Constables::empty, f);
  }

  public static final <E> Optional<? extends ConstantDesc> describeConstable(final Set<? extends E> elements) {
    return describeConstable(elements, CD_Set, Constables::empty, Constables::empty);
  }

  public static final <E> Optional<? extends ConstantDesc> describeConstable(final Set<? extends E> elements,
                                                                             final Function<? super E, ? extends Optional<? extends ConstantDesc>> f) {
    return describeConstable(elements, CD_Set, Constables::empty, f);
  }

  public static final <E> Optional<? extends ConstantDesc> describeConstable(final Set<? extends E> elements,
                                                                             final Function<? super Comparator<?>, ? extends Optional<? extends ConstantDesc>> cf,
                                                                             final Function<? super E, ? extends Optional<? extends ConstantDesc>> f) {
    return describeConstable(elements, CD_Set, cf, f);
  }

  public static final <K, V> Optional<? extends ConstantDesc> describeConstable(final Map<? extends K, ? extends V> map) {
    return describeConstable(map, Constables::empty, Constables::empty, Constables::empty);
  }

  public static final <K, V> Optional<? extends ConstantDesc> describeConstable(final Map<? extends K, ? extends V> map,
                                                                                final Function<? super K, ? extends Optional<? extends ConstantDesc>> kf,
                                                                                final Function<? super V, ? extends Optional<? extends ConstantDesc>> vf) {
    return describeConstable(map, Constables::empty, kf, vf);
  }

  public static final <K, V> Optional<? extends ConstantDesc> describeConstable(final Map<? extends K, ? extends V> map,
                                                                                final Function<? super Comparator<?>, ? extends Optional<? extends ConstantDesc>> cf,
                                                                                final Function<? super K, ? extends Optional<? extends ConstantDesc>> kf,
                                                                                final Function<? super V, ? extends Optional<? extends ConstantDesc>> vf) {
    if (map == null) {
      return Optional.of(NULL);
    } else if (map instanceof Constable c) {
      return c.describeConstable();
    } else if (map.isEmpty()) {
      if (map instanceof SortedMap<? extends K, ? extends V> sm) {
        final Comparator<?> comparator = sm.comparator();
        if (comparator != null && cf != null) {
          final Optional<? extends ConstantDesc> comparatorDesc =
            comparator instanceof Constable c ? c.describeConstable() : cf.apply(comparator);
          if (comparatorDesc != null && comparatorDesc.isPresent()) {
            return
              Optional.of(callStatic(CD_BootstrapMethods,
                                     "immutableSortedMapOf",
                                     MethodTypeDesc.of(CD_SortedMap, CD_Map, CD_Comparator),
                                     callInterfaceStatic(CD_Map, "of", CD_Map),
                                     comparatorDesc.orElseThrow()));
          }
        }
        return Optional.of(callStatic(CD_Collections, "emptySortedMap", MethodTypeDesc.of(CD_SortedMap)));
      }
      return Optional.of(callInterfaceStatic(CD_Map, "of", CD_Map));
    } else {
      final ConstantDesc[] args = new ConstantDesc[map.size()];
      int i = 0;
      for (final Entry<? extends K, ? extends V> entry : map.entrySet()) {
        final Optional<? extends ConstantDesc> e = describeConstable(entry, kf, vf);
        if (e.isEmpty()) {
          return Optional.empty();
        }
        args[i++] = e.orElseThrow();
      }
      final ConstantDesc unsortedMap =
        callInterfaceStatic(CD_Map,
                            "ofEntries",
                            MethodTypeDesc.of(CD_Map, CD_Entry.arrayType()),
                            args);
      if (map instanceof SortedMap<? extends K, ? extends V> sm) {
        final Comparator<?> comparator = sm.comparator();
        if (comparator != null && cf != null) {
          final Optional<? extends ConstantDesc> comparatorDesc =
            comparator instanceof Constable c ? c.describeConstable() : cf.apply(comparator);
          if (comparatorDesc != null && comparatorDesc.isPresent()) {
            return
              Optional.of(callStatic(CD_BootstrapMethods,
                                     "immutableSortedMapOf",
                                     MethodTypeDesc.of(CD_SortedMap, CD_Map, CD_Comparator),
                                     unsortedMap,
                                     comparatorDesc.orElseThrow()));
          }
        }
        return
          Optional.of(callStatic(CD_Collections,
                                 "unmodifiableSortedMap",
                                 MethodTypeDesc.of(CD_SortedMap, CD_SortedMap),
                                 construct(CD_TreeMap,
                                           new ClassDesc[] { CD_Map },
                                           unsortedMap)));
      }
      return Optional.of(unsortedMap);
    }
  }

  public static final <K, V> Optional<? extends ConstantDesc> describeConstable(final Entry<? extends K, ? extends V> entry) {
    return describeConstable(entry, Constables::empty, Constables::empty);
  }

  public static final <K, V> Optional<? extends ConstantDesc> describeConstable(final Entry<? extends K, ? extends V> entry,
                                                                                final Function<? super K, ? extends Optional<? extends ConstantDesc>> kf,
                                                                                final Function<? super V, ? extends Optional<? extends ConstantDesc>> vf) {
    if (entry == null) {
      return Optional.of(NULL);
    } else if (entry instanceof Constable c) {
      return c.describeConstable();
    } else {
      final K k = entry.getKey();
      final Optional<? extends ConstantDesc> key;
      if (k instanceof Constable c) {
        key = c.describeConstable();
      } else if (kf == null) {
        key = Optional.empty();
      } else {
        key = kf.apply(k);
      }
      if (key.isPresent()) {
        final V v = entry.getValue();
        final Optional<? extends ConstantDesc> value;
        if (v instanceof Constable c) {
          value = c.describeConstable();
        } else if (vf == null) {
          value = Optional.empty();
        } else {
          value = vf.apply(v);
        }
        if (value.isPresent()) {
          // Map#entry(K, V)
          return
            Optional.of(callInterfaceStatic(CD_Map,
                                            "entry",
                                            MethodTypeDesc.of(CD_Entry,
                                                              CD_Object, // K erasure
                                                              CD_Object), // V erasure
                                            key.orElseThrow(),
                                            value.orElseThrow()));
        }
      }
      return Optional.empty();
    }
  }

  private static final <E> Optional<? extends ConstantDesc> describeConstable(final Collection<? extends E> elements,
                                                                              final ClassDesc listOrSetClassDesc,
                                                                              final Function<? super Comparator<?>, ? extends Optional<? extends ConstantDesc>> cf,
                                                                              Function<? super E, ? extends Optional<? extends ConstantDesc>> f) {
    if (elements == null) {
      return Optional.of(NULL);
    } else if (elements instanceof Constable c) {
      return c.describeConstable();
    } else if (elements.isEmpty()) {
      if (elements instanceof SortedSet<? extends E> ss) {
        final Comparator<?> comparator = ss.comparator();
        if (comparator != null && cf != null) {
          final Optional<? extends ConstantDesc> comparatorDesc =
            comparator instanceof Constable c ? c.describeConstable() : cf.apply(comparator);
          if (comparatorDesc != null && comparatorDesc.isPresent()) {
            return
              Optional.of(callStatic(CD_BootstrapMethods,
                                     "immutableSortedSetOf",
                                     MethodTypeDesc.of(CD_SortedSet, CD_Collection, CD_Comparator),
                                     callInterfaceStatic(CD_List, "of", CD_List),
                                     comparatorDesc.orElseThrow()));
          }
        }
        return Optional.of(callInterfaceStatic(CD_Collections, "emptySortedSet", CD_SortedSet));
      }
      return Optional.of(callInterfaceStatic(listOrSetClassDesc, "of", listOrSetClassDesc));
    } else {
      if (f == null) {
        f = Constables::empty;
      }
      final int elementsSize = elements.size();
      final ConstantDesc[] args = new ConstantDesc[elementsSize];
      int i = 0;
      for (final E element : elements) {
        final Optional<? extends ConstantDesc> arg;
        if (element instanceof Constable c) {
          arg = c.describeConstable();
        } else {
          arg = f.apply(element);
        }
        if (arg == null || arg.isEmpty()) {
          return Optional.empty();
        }
        args[i++] = arg.orElseThrow();
      }
      final MethodTypeDesc ofMethodTypeDesc;
      if (elementsSize <= 10) {
        // List.of() and Set.of() have explicit polymorphic overrides
        // for parameter counts of up to 10.
        final ClassDesc[] parameterArray = new ClassDesc[elementsSize];
        Arrays.fill(parameterArray, CD_Object); // Object is the erasure of E
        ofMethodTypeDesc = MethodTypeDesc.of(listOrSetClassDesc, parameterArray);
      } else {
        // After 10 parameters, List.of() and Set.of() fall back on
        // varargs.
        ofMethodTypeDesc = MethodTypeDesc.of(listOrSetClassDesc, CD_Object.arrayType());
      }
      if (elements instanceof SortedSet<? extends E> ss) {
        final Comparator<?> comparator = ss.comparator();
        if (comparator != null && cf != null) {
          final Optional<? extends ConstantDesc> comparatorDesc =
            comparator instanceof Constable c ? c.describeConstable() : cf.apply(comparator);
          return
            Optional.of(callStatic(CD_BootstrapMethods,
                                   "immutableSortedSetOf",
                                   MethodTypeDesc.of(CD_SortedSet, CD_Collection, CD_Comparator),
                                   callInterfaceStatic(listOrSetClassDesc,
                                                       "of",
                                                       ofMethodTypeDesc,
                                                       args),
                                   comparatorDesc.orElseThrow()));
        }
        return
          Optional.of(callStatic(CD_Collections,
                                 "unmodifiableSortedSet",
                                 MethodTypeDesc.of(CD_SortedSet, CD_SortedSet),
                                 construct(CD_TreeSet,
                                           new ClassDesc[] { CD_Collection },
                                           callInterfaceStatic(listOrSetClassDesc,
                                                               "of",
                                                               ofMethodTypeDesc,
                                                               args))));
      }
      return
        Optional.of(callInterfaceStatic(listOrSetClassDesc,
                                        "of",
                                        ofMethodTypeDesc,
                                        args));
    }
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

  private static final DynamicConstantDesc<?> call(final DirectMethodHandleDesc.Kind kind, final ClassDesc cd, final String name, final MethodTypeDesc sig, final ConstantDesc... args) {
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

}
