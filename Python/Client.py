import socket

def convert_string_to_binary(text):
    return ''.join(format(ord(c), '08b') for c in text)

def convert_binary_to_string(binary_string):
    chars = []
    for i in range(0, len(binary_string), 8):
        byte = binary_string[i:i+8]
        if len(byte) < 8:
            break
        chars.append(chr(int(byte, 2)))
    return ''.join(chars)

def main():
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.connect(('localhost', 5000))
        print(f"Connected to server at {s.getsockname()}")

        with open('input.txt', 'r') as f:
            for line in f:
                line = line.strip()
                if not line:
                    continue

                print(f"Read from file (Text): {line}")

                binary_str = convert_string_to_binary(line)
                print(f"Converted to binary: {binary_str}")

                s.sendall(binary_str.encode())

                response = s.recv(4096).decode()
                print(f"Received from server (De-stuffed): {response}")

                text = convert_binary_to_string(response)
                print(f"Converted back to original text: {text}")

        # Inform server to stop
        s.sendall("stop".encode())

if __name__ == "__main__":
    main()
