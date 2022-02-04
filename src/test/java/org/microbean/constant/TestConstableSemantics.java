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

import java.lang.constant.Constable;
import java.lang.constant.DynamicConstantDesc;
import java.lang.constant.MethodHandleDesc;
import java.lang.constant.MethodTypeDesc;

import java.lang.invoke.MethodHandles;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;

import static java.lang.constant.ConstantDescs.BSM_INVOKE;
import static java.lang.constant.DirectMethodHandleDesc.Kind.INTERFACE_STATIC;

import static org.microbean.constant.ConstantDescs.CD_Comparator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class TestConstableSemantics {

  private TestConstableSemantics() {
    super();
  }

  @Test
  final void testMapEntryClassName() {
    assertEquals("java.util.Map$Entry", Map.Entry.class.getName());
  }

  @Test
  final void testMap() throws ReflectiveOperationException {
    final Map<String, String> map = Map.of("a", "b", "c", "d");
    assertEquals(map, Constables.describeConstable(map).orElseThrow().resolveConstantDesc(MethodHandles.lookup()));
  }

  @Test
  final void testList() throws ReflectiveOperationException {
    final List<String> list = List.of("a", "b", "c", "d");
    assertEquals(list, Constables.describeConstable(list).orElseThrow().resolveConstantDesc(MethodHandles.lookup()));
  }

  @Test
  final void testSet() throws ReflectiveOperationException {
    final SortedSet<String> set = new TreeSet<>(Comparator.reverseOrder());
    set.addAll(List.of("a", "b", "c", "d"));
    assertNotNull(set.comparator());
    @SuppressWarnings("unchecked")
    final SortedSet<String> result =
      (SortedSet<String>)Constables.describeConstable(set,
                                                      c -> Optional.of(DynamicConstantDesc.of(BSM_INVOKE,
                                                                                              MethodHandleDesc.ofMethod(INTERFACE_STATIC,
                                                                                                                        CD_Comparator,
                                                                                                                        "reverseOrder",
                                                                                                                        MethodTypeDesc.of(CD_Comparator)))),
                                                      Constable::describeConstable)
      .orElseThrow()
      .resolveConstantDesc(MethodHandles.lookup());
    assertThrows(UnsupportedOperationException.class, () -> result.add("e"));
    assertEquals(set.comparator(), result.comparator());
    assertEquals(set, result);
  }

}
