class PipelineMock {
  def commands = []

  def sh(command) {
    commands.add(command)
  }

  def jobDsl(dsl) {
    return dsl
  }
}
