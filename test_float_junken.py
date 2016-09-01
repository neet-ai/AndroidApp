import math
import random
import codecs as cod
from janome.tokenizer import Tokenizer
from gensim.models import word2vec

import numpy as np
import chainer
from chainer import cuda, Function, gradient_check, report, training, utils, Variable
from chainer import datasets, iterators, optimizers, serializers
from chainer import Link, Chain, ChainList
import chainer.functions as F
import chainer.links as L
from chainer.training import extensions


#########################################
#########################################
#########################################
#############              ##############
#############     定義     ##############
#############              ##############
#########################################
#########################################
#########################################


def num2id(num):	# 数値をベクトルに変える関数
	if num == 0:
		out = [1, 0, 0]
	elif num == 1:
		out = [0, 1, 0]
	else:
		out = [0, 0, 1]
	return np.array([out], dtype=np.float32);

def word2id(word):	# 単語をベクトルに変える関数
	if word == "グー" or word == "0":
		out = 0
	elif word == "チョキ" or word == "1":
		out = 1
	else:
		out = 2
	return num2id(out)

def id2word(id):	# ベクトルを単語に変える関数
	num = id.argmax()
	if num == 0:
		out = "グー"
	elif num == 1:
		out = "チョキ"
	else:
		out = "パー"
	return out
	
def compute_loss(id_list): #誤差を出力する関数
	loss = 0
	for cur_id, next_id in zip(id_list, id_list[1:]):
		loss += model_rnn(rnn(cur_id), next_id)
	return loss

# LSTMを定義するクラス
class myRNN(Chain):
	def __init__(self):		# ネットワークの定義
		super(myRNN, self).__init__(
			ent = L.Linear(3, 2),
			mid = L.LSTM(2, 2),
			nn1 = L.Linear(2, 2),
			nn2 = L.Linear(2, 2),
			out = L.Linear(2, 3),
		)
		
	def reset_state(self):	# LSTM入力のリセット
		self.mid.reset_state()
		
	def __call__(self, cur_id):	# LSTMセット一つ分の定義
		xx = self.ent(cur_id)
		h1 = self.mid(xx)
		h2 = self.nn1(h1)
		h3 = self.nn2(h2)
		yy = self.out(h3)
		return yy

# 学習定義クラス
class Regression(Chain):
	def __init__(self, predictor):
		super(Regression, self).__init__(predictor=predictor)

	def __call__(self, x, t):
		y = self.predictor(x)
		loss = F.mean_squared_error(y, t)
		#accuracy = F.accuracy(y, t)
		#report({'loss': loss, 'accuracy': accuracy}, self)
		return loss

# RNNの準備
rnn = myRNN()
model_rnn = Regression(rnn)
optimizer = optimizers.Adam()
optimizer.setup(model_rnn)

def trains(): 	# 学習
	for i in range(TRAIN_NUM):
		# デバッグ表示
		if((i+1) % 200 == 0):
			print(">>学習回数：" + str(i+1) + " loss：" + str(compute_loss(data).data) )
		# データ生成
		a = random.randint(0,2)
		b = random.randint(0,2)
		if a == b:
			c = a
		else:
			c = 3 - a - b
		nums = [a, b, c]
		data = np.array([num2id(j) for j in nums])
		#更新
		rnn.reset_state()
		k = 0
		for j in range(1):
			rnn(data[j])
			k += 1
		optimizer.update(compute_loss, data[k:])
	serializers.save_npz(MODEL_NAME, model_rnn)
	serializers.save_npz(STATE_NAME, optimizer)


#########################################
#########################################
#########################################
#############              ##############
#############  メイン処理  ##############
#############              ##############
#########################################
#########################################
#########################################

TRAIN_NUM = 10000
MODEL_NAME = "float_junk.model"
STATE_NAME = "float_junk.state"

print("\n>>モデルを読み込む:a / 読み込み＆学習:b / 一から学習:c ↓")
str0 = input()
if str0 != "c":
	serializers.load_npz(MODEL_NAME, model_rnn)
	serializers.load_npz(STATE_NAME, optimizer)
if str0 == "b" or str0 == "c":
	print("\n>>学習実行中...")
	trains()

while True:
	# 入力
	rnn.reset_state()
	print("\n>>一つ目の手を入力↓")
	str1 = input()
	a1 = word2id(str1)
	print("\n>>二つ目の手を入力↓")
	str1 = input()
	b1 = word2id(str1)
	ent = []
	ent.append(a1)
	ent.append(b1)
	
	# 計算
	ans = []
	for i in ent:
		out = rnn(i)
		ans.append(out)
	
	# 出力
	data1 = ans[1].data
	print("\n>>data1：\n" + str(data1))
	print("\n>>計算結果：" + id2word(data1))
	"""
	# 列1の最大値
	data2 = np.array([0.0, 0.0, 0.0])
	count = 0
	for i in data1:
		data2[count] = i[1]
		count += 1
	# 行の総和
	data3 = np.sum(data1, axis=1)
	data3 = data3.T
	# 列0の最小値
	data4 = np.array([0.0, 0.0, 0.0])
	count = 0
	for i in data1:
		data4[count] = i[0] * (-1)
		count += 1
	# 列1-列0の最大値
	data5 = np.array([0.0, 0.0, 0.0])
	count = 0
	for i in data1:
		data5[count] = i[1] - i[0]
		count += 1
	print("\n>>data2(列1の最大)：" + str(data2))
	print(">>計算結果(data2)：" + str( id2word(data2) ) )
	print("\n>>data3(行の総和)：" + str(data3))
	print(">>計算結果(data3)：" + str( id2word(data3) ) )
	print("\n>>data4(列0の最小)：" + str(data4))
	print(">>計算結果(data4)：" + str( id2word(data4) ) )
	print("\n>>data5(列1-列0)：" + str(data5))
	print(">>計算結果(data5)：" + str( id2word(data5) ) )
	"""
	
	