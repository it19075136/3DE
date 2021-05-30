import logging
import pyrebase
from sentence_transformers import SentenceTransformer

from utils.basic import *
from utils.ts_ss import triangle_sector_similarity
from utils.pairwise import bert_pairwise_cos_sim


class BERTCalculator:
    def __init__(self, config, sentences):
        self.sentences = sentences
        self.method = config.method
        self.verbose = config.verbose

    def calculate(self):
        methods = {
            "cosine": cosine_sim,
            "manhattan": manhattan_dist,
            "euclidean": euclidean_dist,
            "angular": angular_distance,
            "inner": inner_product,
            "ts-ss": triangle_sector_similarity,
            "pairwise": bert_pairwise_cos_sim,
            "pairwise-idf": bert_pairwise_cos_sim,
        }

        if self.method not in methods:
            logging.error(f"The method you chosen is not supported yet.")
            return False

        if "pairwise" in self.method:
            # Use-case of Pairwise cosine similarity with IDF
            if "idf" in self.method:
                similarity = bert_pairwise_cos_sim(self.sentences, idf=True)
                plot_similarity(self.sentences, similarity, self.method)
                return

            # Use-case of Pairwise cosine similarity
            similarity = bert_pairwise_cos_sim(self.sentences)
            plot_similarity(self.sentences, similarity, self.method)
            return

        model = SentenceTransformer("bert-base-nli-mean-tokens")

        if self.verbose:
            logging.info(f"Now embedding sentence...")

        config = {
            "apiKey": "AIzaSyBdXTCnelDAEe7lSl8UwguuDD0efU8ezxY",
            "authDomain": "project-3de-eb7dd.firebaseapp.com",
            "databaseURL": "https://project-3de-eb7dd-default-rtdb.firebaseio.com",
            "projectId": "project-3de-eb7dd",
            "storageBucket": "project-3de-eb7dd.appspot.com",
            "messagingSenderId": "967906533329",
            "appId": "1:967906533329:web:00defde43617cb3b148af0"
        }

        firebase = pyrebase.initialize_app(config)
        db = firebase.database()
        while(True):
            level = db.child("Test").child("Result").child("level").get()
            result = db.child("Test").child("Result").child("result").get()
            test = db.child("Test").child("Result").child("test").get()
            if(level.val()!="y" and test.val()!="z"):
                embed_sentences = np.asarray(model.encode([self.sentences[int(level.val())-1]]))
                embeddingsRecvied =np.asarray(model.encode([test.val()]))
                method = methods[self.method]
                if self.verbose:
                    logging.info(f"Calculating similarity between sentences...")
                similarity = method(embeddingsRecvied, embed_sentences)
                value = plot_similarity(self.sentences, similarity, self.method)
                db.child("Test").child("Result").child("test").set("z")
                db.child("Test").child("Result").child("result").set(str(value))
                db.child("Test").child("Result").child("level").set("y")
            else:
                continue

