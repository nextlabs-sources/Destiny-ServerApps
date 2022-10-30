export interface Task {
  progress: number;
  taskDescription: string;
  group: string;
}

export interface ProgressMessage {
  task: Task;
  installError: boolean;
}
