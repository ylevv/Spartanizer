package il.org.spartan.refactoring.utils;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

/**
 * Determines whether an {@link ASTNode} is spartanization disabled. In the
 * current implementation, only instances of {@link BodyDeclaration} may be
 * disabled, and only via their {@link Javadoc} comment
 *
 * @author Ori Roth
 * @since 2016/05/13
 */
public class Disable {
  final Set<ASTNode> dns;
  /**
   * Disable spartanization identifier, used by the programmer to indicate a
   * method/class/code line not to be spartanized
   */
  public final static String dsi = "@DisableSpartan";

  protected Disable(CompilationUnit u) {
    dns = new HashSet<>();
    if (u == null)
      return;
    u.accept(new BodyDeclarationVisitor(dns));
  }
  /**
   * @param n node
   * @return true iff spartanization is disabled for n
   */
  public boolean check(ASTNode n) {
    ASTNode p = n;
    while (p != null) {
      if (dns.contains(p))
        return true;
      p = p.getParent();
    }
    return false;
  }

  private static class BodyDeclarationVisitor extends ASTVisitor {
    Set<ASTNode> dns;

    BodyDeclarationVisitor(Set<ASTNode> dns) {
      this.dns = dns;
    }
    @Override public boolean visit(AnnotationTypeDeclaration d) {
      return go(d);
    }
    @Override public boolean visit(EnumDeclaration d) {
      return go(d);
    }
    @Override public boolean visit(TypeDeclaration d) {
      return go(d);
    }
    @Override public boolean visit(AnnotationTypeMemberDeclaration d) {
      return go(d);
    }
    @Override public boolean visit(EnumConstantDeclaration d) {
      return go(d);
    }
    @Override public boolean visit(FieldDeclaration d) {
      return go(d);
    }
    @Override public boolean visit(Initializer i) {
      return go(i);
    }
    @Override public boolean visit(MethodDeclaration d) {
      return go(d);
    }
    private boolean go(BodyDeclaration d) {
      final Javadoc j = d.getJavadoc();
      if (j == null || !j.toString().contains(dsi))
        return true;
      dns.add(d);
      return false;
    }
  }
}