import cv2
import mediapipe as mp
import time
import ctypes
import os

# ✅ Launch Hill Climb Racing (Microsoft Store version)
os.system('start shell:AppsFolder\\FINGERSOFT.HILLCLIMBRACING_r6rtpscs7gwyg!App')

# Setup for key press simulation (Windows only)
SendInput = ctypes.windll.user32.SendInput

# Define keys
KEY_LEFT = 0x25   # Left arrow key = Brake
KEY_RIGHT = 0x27  # Right arrow key = Accelerate

# Structures for key simulation
PUL = ctypes.POINTER(ctypes.c_ulong)

class KeyBdInput(ctypes.Structure):
    _fields_ = [("wVk", ctypes.c_ushort),
                ("wScan", ctypes.c_ushort),
                ("dwFlags", ctypes.c_ulong),
                ("time", ctypes.c_ulong),
                ("dwExtraInfo", PUL)]

class HardwareInput(ctypes.Structure):
    _fields_ = [("uMsg", ctypes.c_ulong),
                ("wParamL", ctypes.c_short),
                ("wParamH", ctypes.c_ushort)]

class MouseInput(ctypes.Structure):
    _fields_ = [("dx", ctypes.c_long),
                ("dy", ctypes.c_long),
                ("mouseData", ctypes.c_ulong),
                ("dwFlags", ctypes.c_ulong),
                ("time", ctypes.c_ulong),
                ("dwExtraInfo", PUL)]

class Input_I(ctypes.Union):
    _fields_ = [("ki", KeyBdInput),
                ("mi", MouseInput),
                ("hi", HardwareInput)]

class Input(ctypes.Structure):
    _fields_ = [("type", ctypes.c_ulong),
                ("ii", Input_I)]

def press_key(hexKeyCode):
    extra = ctypes.c_ulong(0)
    ii_ = Input_I()
    ii_.ki = KeyBdInput(hexKeyCode, 0x48, 0, 0, ctypes.pointer(extra))
    x = Input(ctypes.c_ulong(1), ii_)
    SendInput(1, ctypes.pointer(x), ctypes.sizeof(x))

def release_key(hexKeyCode):
    extra = ctypes.c_ulong(0)
    ii_ = Input_I()
    ii_.ki = KeyBdInput(hexKeyCode, 0x48, 2, 0, ctypes.pointer(extra))
    x = Input(ctypes.c_ulong(1), ii_)
    SendInput(1, ctypes.pointer(x), ctypes.sizeof(x))

# Mediapipe setup for Face Mesh
mpFaceMesh = mp.solutions.face_mesh
faceMesh = mpFaceMesh.FaceMesh(max_num_faces=1, min_detection_confidence=0.7)
mpDraw = mp.solutions.drawing_utils

cap = cv2.VideoCapture(0)
prev_gesture = None
gesture_text = ""
initial_nose_y = None

# Define the indexes for the border joints
border_indexes = [
    10,  338, 297, 332, 284, 251, 389, 356, 454, 323, 361, 288, 397, 365, 379, 378, 
    400, 377, 152, 148, 176, 149, 150, 136, 172, 58, 132, 93, 234, 127, 162, 21, 54, 
    103, 67, 109
]

def get_gesture(face_landmarks, img_height, img_width):
    global initial_nose_y

    # Get the vertical position of the nose tip
    nose_tip = face_landmarks.landmark[1]
    nose_y = int(nose_tip.y * img_height)

    # Set the initial nose_y position for reference
    if initial_nose_y is None:
        initial_nose_y = nose_y

    # Determine head up or down
    if nose_y < initial_nose_y - 15:  # Threshold to detect head up
        return "GAS"
    elif nose_y > initial_nose_y + 15:  # Threshold to detect head down
        return "BRAKE"
    else:
        return "NEUTRAL"

while True:
    success, img = cap.read()
    if not success:
        break

    imgRGB = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
    results = faceMesh.process(imgRGB)

    if results.multi_face_landmarks:
        for faceLms in results.multi_face_landmarks:
            gesture = get_gesture(faceLms, img.shape[0], img.shape[1])

            if gesture != prev_gesture:
                if gesture == "GAS":
                    press_key(KEY_RIGHT)
                    release_key(KEY_LEFT)
                    print("Accelerate")
                elif gesture == "BRAKE":
                    press_key(KEY_LEFT)
                    release_key(KEY_RIGHT)
                    print("Brake")
                elif gesture == "NEUTRAL":
                    release_key(KEY_LEFT)
                    release_key(KEY_RIGHT)
                    print("Neutral")
                prev_gesture = gesture
                gesture_text = gesture

            # Draw border joints
            for idx in border_indexes:
                landmark = faceLms.landmark[idx]
                x = int(landmark.x * img.shape[1])
                y = int(landmark.y * img.shape[0])
                cv2.circle(img, (x, y), 2, (0, 255, 0), -1)

    else:
        release_key(KEY_LEFT)
        release_key(KEY_RIGHT)
        prev_gesture = None
        gesture_text = "No Face"

    # Show gesture on screen
    cv2.putText(img, f"Gesture: {gesture_text}", (10, 40),
                cv2.FONT_HERSHEY_SIMPLEX, 1.2, (0, 255, 0), 3)

    cv2.imshow("Head Movement Controller", img)
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

cap.release()
cv2.destroyAllWindows()
