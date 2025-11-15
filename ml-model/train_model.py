import os

import joblib
import numpy as np
import pandas as pd
import tensorflow as tf
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler
from tensorflow.keras import Sequential
from tensorflow.keras import layers
from tensorflow.keras.layers import Dense, Dropout


def create_synthetic_data(n_samples=50000):
    print(f"Generating {n_samples} synthetic samples...")

    legit_data = {
        'amount': np.random.normal(loc=100, scale=50, size=n_samples).clip(min=1),
        'tx_hour': np.random.randint(0, 24, size=n_samples),
        'device_score': np.random.randint(70, 100, size=n_samples),
        'is_fraud': np.zeros(n_samples, dtype=int)
    }

    n_fraud = int(n_samples * 0.05)
    fraud_indices = np.random.choice(n_samples, n_fraud, replace=False)

    legit_data['amount'][fraud_indices] = np.random.normal(loc=800, scale=300, size=n_fraud).clip(min=100)
    legit_data['tx_hour'][fraud_indices] = np.random.randint(0, 6, size=n_fraud)
    legit_data['device_score'][fraud_indices] = np.random.randint(0, 50, size=n_fraud)
    legit_data['is_fraud'][fraud_indices] = 1

    df = pd.DataFrame(legit_data)
    return df.sample(frac=1).reset_index(drop=True)

df = create_synthetic_data()

features = ['amount', 'tx_hour', 'device_score']
target = 'is_fraud'

X = df[features]
y = df[target]

X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42, stratify=y)
scaler = StandardScaler()
X_train = scaler.fit_transform(X_train)
X_test = scaler.transform(X_test)

print(f"Training data shape: {X_train.shape}")
print(f"Testing data shape: {X_test.shape}")

model = Sequential([
    Dense(16, activation='relu', input_shape=(X_train.shape[1],)),
    Dropout(0.2),
    Dense(8, activation='relu'),
    Dense(1, activation='sigmoid')
])

model.compile(
    optimizer='adam',
    loss='binary_crossentropy',
    metrics=['accuracy', tf.keras.metrics.Precision(), tf.keras.metrics.Recall()]
)

model.summary()

print("\nStarting model training")
history = model.fit(
    X_train,
    y_train,
    validation_split=0.2,
    epochs=50,
    batch_size=32,
    verbose=1
)

print("\nEvaluating model on test data...")
results = model.evaluate(X_test, y_test, verbose=1)
print(f"Test Loss: {results[0]:.4f}")
print(f"Test Accuracy: {results[1]:.4f}")
print(f"Test Precision: {results[2]:.4f}")
print(f"Test Recall: {results[3]:.4f}")

output_dir = 'fraud_model'
os.makedirs(output_dir, exist_ok=True)

model_path = os.path.join(output_dir, 'model.keras')
model.save(model_path)
print(f"\nModel saved to {model_path}")

scaler_path = os.path.join(output_dir, 'scaler.joblib')
joblib.dump(scaler, scaler_path)
print(f"Scaler saved to {scaler_path}")