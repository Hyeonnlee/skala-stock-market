import sys
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from tensorflow.keras.models import load_model
from sklearn.preprocessing import MinMaxScaler
import os

def load_data(file_path):
    df = pd.read_csv(file_path)
    df = df.sort_values(by='Date')  # 날짜 기준 정렬
    df['Close'] = pd.to_numeric(df['Close'], errors='coerce')
    df = df.dropna(subset=['Close'])  # 결측치 제거
    return df[['Close']].values

def prepare_sequences(data, sequence_length=30):
    X = []
    for i in range(sequence_length, len(data)):
        X.append(data[i-sequence_length:i])
    return np.array(X)

def main():
    if len(sys.argv) < 2:
        print("Usage: python predict.py input.csv")
        sys.exit(1)

    input_csv = sys.argv[1]

    # 모델 경로
    model_path = 'lstm.keras'
    if not os.path.exists(model_path):
        print(f"모델 파일이 존재하지 않습니다: {model_path}")
        sys.exit(1)

    # 데이터 로드 및 전처리
    raw_data = load_data(input_csv)
    scaler = MinMaxScaler()
    scaled_data = scaler.fit_transform(raw_data)

    sequence_length = 30
    X = prepare_sequences(scaled_data, sequence_length)

    if len(X) == 0:
        print("데이터가 부족하여 예측할 수 없습니다.")
        sys.exit(1)

    # 모델 로드 및 예측
    model = load_model(model_path)
    predictions = model.predict(X)
    predictions = scaler.inverse_transform(predictions)

    # 실제와 예측 시각화
    plt.figure(figsize=(10, 5))
    real = raw_data[sequence_length:]
    plt.plot(real, label='Real Price')
    plt.plot(predictions, label='Predicted Price')
    plt.title('Stock Prediction')
    plt.xlabel('Days')
    plt.ylabel('Price')
    plt.legend()
    plt.tight_layout()
    plt.savefig('output.png')  # 결과 이미지 저장
    print("예측 완료 및 이미지 저장 완료")

if __name__ == "__main__":
    main()
