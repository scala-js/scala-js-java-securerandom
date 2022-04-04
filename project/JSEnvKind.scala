package build

sealed abstract class JSEnvKind

object JSEnvKind {
  case object NodeJS extends JSEnvKind
  case object JSDOMNodeJS extends JSEnvKind
  case object Firefox extends JSEnvKind
  case object Chrome extends JSEnvKind
}
