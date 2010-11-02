/*
 * Copyright 2010 Henry Coles
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and limitations under the License. 
 */

package org.pitest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.pitest.extension.ResultCollector;
import org.pitest.extension.TestFilter;
import org.pitest.extension.TestUnit;
import org.pitest.functional.Option;

public class MultipleTestGroup implements TestUnit {

  private final static Description   groupDescription = new Description(
                                                          "MultiGroup",
                                                          MultipleTestGroup.class,
                                                          null);

  private static final long          serialVersionUID = 1L;

  private final Collection<TestUnit> children;

  public MultipleTestGroup(final Collection<TestUnit> children) {
    this.children = children;
  }

  public MultipleTestGroup() {
    this(new ArrayList<TestUnit>());
  }

  public void add(final TestUnit tu) {
    this.children.add(tu);
  }

  public boolean contains(final TestUnit value) {
    for (final TestUnit each : this.children) {
      if (each.description().equals(value.description())) {
        return true;
      }
    }
    return false;
  }

  public Description description() {
    return groupDescription;
  }

  public void execute(final ClassLoader loader, final ResultCollector rc) {
    for (final TestUnit each : this.children) {
      each.execute(loader, rc);
      if (rc.shouldExit()) {
        break;
      }
    }

  }

  public Iterator<TestUnit> iterator() {
    return this.children.iterator();
  }

  public Option<TestUnit> filter(final TestFilter filter) {

    final Collection<TestUnit> filtered = new ArrayList<TestUnit>(this.children
        .size());
    for (final TestUnit each : this.children) {
      final Option<TestUnit> tu = each.filter(filter);
      for (final TestUnit value : tu) {
        filtered.add(value);
      }
    }

    if (filtered.isEmpty()) {
      return Option.none();
    } else {
      return Option.<TestUnit> someOrNone(new MultipleTestGroup(filtered));
    }

  }

}
