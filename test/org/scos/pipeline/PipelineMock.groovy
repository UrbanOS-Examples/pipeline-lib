class PipelineMock {
  def commands = []

  def sh(command) {
    commands.add(command)
  }

}
