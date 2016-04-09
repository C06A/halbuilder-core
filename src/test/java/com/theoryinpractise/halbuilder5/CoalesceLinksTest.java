package com.theoryinpractise.halbuilder5;

import javaslang.collection.List;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;
import static com.theoryinpractise.halbuilder5.LinkListSubject.assertAboutLinkLists;

public class CoalesceLinksTest {

  @Test
  public void testNonCoalesceLinks() {

    ResourceRepresentation<Void> resource = ResourceRepresentation.empty("/foo").withLink("bar", "/bar").withLink("foo", "/bar");

    List<Link> links = resource.getLinks(false);
    assertThat(links).isNotEmpty();

    assertAboutLinkLists(links).containsRelCondition("bar");
    assertAboutLinkLists(links).containsRelCondition("foo");

    assertThat(resource.getLinksByRel("bar")).isNotNull();
    assertAboutLinkLists(resource.getLinksByRel("bar")).containsRelCondition("bar");
    assertAboutLinkLists(resource.getLinksByRel("bar")).doesNotContainRelCondition("foo");

    assertThat(resource.getLinksByRel("foo")).isNotNull();
    assertAboutLinkLists(resource.getLinksByRel("foo")).containsRelCondition("foo");
    assertAboutLinkLists(resource.getLinksByRel("foo")).doesNotContainRelCondition("bar");
  }

  @Test
  public void testCoalesceLinks() {

    ResourceRepresentation<Void> resource = ResourceRepresentation.empty("/foo").withLink("bar", "/bar").withLink("foo", "/bar");

    List<Link> links = resource.getLinks(true);

    assertThat(links).isNotEmpty();
    assertAboutLinkLists(links).containsRelCondition("bar foo");
    assertAboutLinkLists(links).containsRelCondition("bar");
    assertAboutLinkLists(links).containsRelCondition("foo");

    assertThat(resource.getLinksByRel("bar")).isNotNull();
    assertAboutLinkLists(resource.getLinksByRel("bar")).containsRelCondition("bar");
    assertAboutLinkLists(resource.getLinksByRel("bar")).doesNotContainRelCondition("foo");

    assertThat(resource.getLinksByRel("foo")).isNotNull();
    assertAboutLinkLists(resource.getLinksByRel("foo")).doesNotContainRelCondition("barf");
    assertAboutLinkLists(resource.getLinksByRel("foo")).containsRelCondition("foo");
  }

  @Test
  public void testSpacedRelsSeparateLinks() {

    ResourceRepresentation<Void> resource = ResourceRepresentation.empty("/foo");

    try {
      resource.withLink("bar foo", "/bar");
      throw new AssertionError("We should fail to add a space separated link rel.");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void testMultiSpacedRelsSeparateLinks() {

    ResourceRepresentation<Void> resource = ResourceRepresentation.empty("/foo");

    try {
      resource.withLink("bar                  foo", "/bar");
      throw new AssertionError("We should fail to add a space separated link rel.");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void testRelLookupsWithNullFail() {
    try {
      ResourceRepresentation<Void> resource = ResourceRepresentation.empty("/foo").withLink("bar foo", "/bar");

      resource.getLinkByRel((String) null);
      throw new AssertionError("Should fail");
    } catch (IllegalArgumentException e) {
      // ignore
    }
  }

  @Test
  public void testRelLookupsWithEmptyRelFail() {
    try {
      ResourceRepresentation<Void> resource = ResourceRepresentation.empty("/foo").withLink("bar", "/bar");

      resource.getLinkByRel("");

      throw new AssertionError("Should fail");
    } catch (IllegalArgumentException e) {
      // ignore
    }
  }

  @Test
  public void testRelLookupsWithSpacesFail() {
    try {
      ResourceRepresentation<Void> resource = ResourceRepresentation.empty("/foo").withLink("bar", "/bar");

      resource.getLinkByRel("test fail");
      throw new AssertionError("Should fail");
    } catch (IllegalArgumentException e) {
      // ignore
    }
  }
}
