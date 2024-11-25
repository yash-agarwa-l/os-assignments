#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/mman.h>
#include <sys/stat.h>
#include <string.h>
#include <semaphore.h>

#define SHARED_MEMORY_NAME "/shared_memory"
#define BUFFER_SIZE 10

typedef struct {
    int buffer[BUFFER_SIZE];
    int in, out;
    sem_t empty, full, mutex;
} SharedBuffer;

int main() {
    int shm_fd = shm_open(SHARED_MEMORY_NAME, O_CREAT | O_RDWR, 0666);
    ftruncate(shm_fd, sizeof(SharedBuffer));

    SharedBuffer *sharedBuffer = (SharedBuffer *)mmap(0, sizeof(SharedBuffer), PROT_READ | PROT_WRITE, MAP_SHARED, shm_fd, 0);

    sharedBuffer->in = sharedBuffer->out = 0;
    sem_init(&sharedBuffer->empty, 1, BUFFER_SIZE);
    sem_init(&sharedBuffer->full, 1, 0);
    sem_init(&sharedBuffer->mutex, 1, 1);

    if (fork() == 0) {
        // Producer
        for (int i = 0; i < 20; i++) {
            sem_wait(&sharedBuffer->empty);
            sem_wait(&sharedBuffer->mutex);

            sharedBuffer->buffer[sharedBuffer->in] = i;
            printf("Produced: %d\n", i);
            sharedBuffer->in = (sharedBuffer->in + 1) % BUFFER_SIZE;

            sem_post(&sharedBuffer->mutex);
            sem_post(&sharedBuffer->full);
            sleep(1);
        }
    } else {
        // Consumer
        for (int i = 0; i < 20; i++) {
            sem_wait(&sharedBuffer->full);
            sem_wait(&sharedBuffer->mutex);

            int item = sharedBuffer->buffer[sharedBuffer->out];
            printf("Consumed: %d\n", item);
            sharedBuffer->out = (sharedBuffer->out + 1) % BUFFER_SIZE;

            sem_post(&sharedBuffer->mutex);
            sem_post(&sharedBuffer->empty);
            sleep(2);
        }

        shm_unlink(SHARED_MEMORY_NAME);
    }

    return 0;
}


//Parent-Child Communication Using Pipes
#include <stdio.h>
#include <unistd.h>
#include <string.h>

int main() {
    int pipefd[2];
    pid_t pid;
    char writeMsg[] = "Hello from Parent!";
    char readBuffer[50];

    if (pipe(pipefd) == -1) {
        perror("Pipe failed");
        return 1;
    }

    pid = fork();
    if (pid < 0) {
        perror("Fork failed");
        return 1;
    }

    if (pid == 0) {
        // Child process
        close(pipefd[1]); // Close unused write end
        read(pipefd[0], readBuffer, sizeof(readBuffer));
        printf("Child received: %s\n", readBuffer);
        close(pipefd[0]);
    } else {
        // Parent process
        close(pipefd[0]); // Close unused read end
        write(pipefd[1], writeMsg, strlen(writeMsg) + 1);
        close(pipefd[1]);
    }

    return 0;
}
//3. IPC Using Message Queues

#include <stdio.h>
#include <sys/ipc.h>
#include <sys/msg.h>
#include <string.h>
#include <stdlib.h>

#define MESSAGE_SIZE 100

struct message {
    long messageType;
    char messageText[MESSAGE_SIZE];
};

int main() {
    key_t key;
    int msgid;

    // Generate unique key
    key = ftok("progfile", 65);

    // Create message queue
    msgid = msgget(key, 0666 | IPC_CREAT);

    if (fork() == 0) {
        // Producer (Sender)
        struct message msg;
        msg.messageType = 1;

        for (int i = 0; i < 5; i++) {
            snprintf(msg.messageText, MESSAGE_SIZE, "Message %d from Producer", i + 1);
            msgsnd(msgid, &msg, sizeof(msg.messageText), 0);
            printf("Sent: %s\n", msg.messageText);
            sleep(1);
        }
    } else {
        // Consumer (Receiver)
        struct message msg;

        for (int i = 0; i < 5; i++) {
            msgrcv(msgid, &msg, sizeof(msg.messageText), 1, 0);
            printf("Received: %s\n", msg.messageText);
        }

        // Destroy the message queue
        msgctl(msgid, IPC_RMID, NULL);
    }

    return 0;
}
