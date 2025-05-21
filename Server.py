import socket
import threading

def bit_de_stuffing(stuffed_string):
    destuffed = []
    count = 0

    i = 0
    while i < len(stuffed_string):
        bit = stuffed_string[i]

        if bit == '1':
            count += 1
            destuffed.append(bit)
            i += 1
        elif bit == '0':
            if count == 5:
                # skip this stuffed zero
                count = 0
                i += 1
            else:
                destuffed.append(bit)
                count = 0
                i += 1
        else:
            # Ignore unexpected chars
            i += 1

    return ''.join(destuffed)

def handle_client(client_socket, addr):
    print(f"[+] New connection from {addr}")

    try:
        while True:
            data = client_socket.recv(4096).decode()
            if not data or data.strip().lower() == "stop":
                print(f"[-] Connection closed by {addr}")
                break

            print(f"Received stuffed data from {addr}: {data}")

            destuffed = bit_de_stuffing(data)
            print(f"De-stuffed data: {destuffed}")

            client_socket.send(destuffed.encode())
    except Exception as e:
        print(f"[!] Error with client {addr}: {e}")
    finally:
        client_socket.close()

def main():
    server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server.bind(('localhost', 5000))
    server.listen(5)
    print("[*] Server listening on port 5000")

    try:
        while True:
            client_sock, addr = server.accept()
            client_thread = threading.Thread(target=handle_client, args=(client_sock, addr))
            client_thread.start()
    except KeyboardInterrupt:
        print("\n[!] Server shutting down.")
    finally:
        server.close()

if __name__ == "__main__":
    main()
