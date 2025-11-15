import os

import joblib
import numpy as np
from flask import Flask, request, jsonify
from tensorflow.keras.models import load_model

print("Loading model and scaler...")
MODEL_DIR = 'fraud_model'
MODEL_PATH = os.path.join(MODEL_DIR, 'model.keras')
SCALER_PATH = os.path.join(MODEL_DIR, 'scaler.joblib')

try:
    model = load_model(MODEL_PATH)
    print("Model loaded")
except Exception as e:
    print(f"Error loading model: {e}")
    model = None

try:
    scaler = joblib.load(SCALER_PATH)
    print("Scaler loaded")
except Exception as e:
    print(f"Error loading scaler: {e}")
    scaler = None

app = Flask(__name__)

@app.route("/predict", methods=["POST"])
def predict():
    if not model or not scaler:
        return jsonify({"error": "model not loaded"}), 500

    try:
        data = request.get_json(force=True)
        features = [
            data['amount'],
            data['tx_hour'],
            data['device_score']
        ]

        input_data = np.array([features])
        input_data_scaled = scaler.transform(input_data)
        fraud_score = model.predict(input_data_scaled)[0][0]
        is_fraud = bool(fraud_score > .5)
        response = {
            'isFraud': is_fraud,
            'fraudScore': float(fraud_score)
        }

        return jsonify(response), 200

    except KeyError as e:
        return jsonify({"error": f"Missing feature in request: {str(e)}"}), 404
    except Exception as e:
        return jsonify({"error": str(e)}), 500

if __name__ == "__main__":
    app.run(debug=True, port=5000, host='0.0.0.0')