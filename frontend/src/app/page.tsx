"use client";

import { useEffect, useState } from "react";

type HealthResponse = {
  status: string;
  service: string;
};

type Quiz = {
  id: string;
  title: string;
};

type QuizResponse = {
  quizzes: Quiz[];
};

export default function Home() {
  const [health, setHealth] =
    useState<HealthResponse | null>(null);

  const [quizzes, setQuizzes] =
    useState<Quiz[]>([]);

  const [loading, setLoading] =
    useState(false);

  const [error, setError] =
    useState("");

  async function loadData() {
    setLoading(true);
    setError("");

    try {
      const healthResponse =
        await fetch(
          "http://localhost:8080/health"
        );

      if (!healthResponse.ok) {
        throw new Error(
          "Health request failed"
        );
      }

      const healthData: HealthResponse =
        await healthResponse.json();

      setHealth(healthData);

      const quizResponse =
        await fetch(
          "http://localhost:8080/api/quizzes"
        );

      if (!quizResponse.ok) {
        throw new Error(
          "Quiz request failed"
        );
      }

      const quizData: QuizResponse =
        await quizResponse.json();

      setQuizzes(quizData.quizzes);

    } catch (error) {

      console.error(error);

      setError(
        "Không kết nối được Netty backend."
      );

    } finally {

      setLoading(false);
    }
  }

  useEffect(() => {
    loadData();
  }, []);

  return (
    <main className="min-h-screen bg-slate-950 text-white">

      <div className="max-w-5xl mx-auto px-6 py-16">

        <div className="mb-12">

          <p className="text-sm uppercase tracking-[0.3em] text-blue-400">
            Netty Quiz Lab
          </p>

          <h1 className="text-5xl font-black mt-4">
            HTTP Server
          </h1>

          <p className="text-slate-400 mt-4 text-lg">
            Browser → HTTP → Netty
          </p>

        </div>

        <div className="grid md:grid-cols-2 gap-6">

          {/* Backend status */}

          <section className="rounded-2xl border border-slate-800 bg-slate-900 p-6">

            <h2 className="text-xl font-bold">
              Backend Status
            </h2>

            <div className="mt-6">

              {loading && (
                <p className="text-yellow-400">
                  Connecting...
                </p>
              )}

              {!loading && error && (
                <p className="text-red-400">
                  {error}
                </p>
              )}

              {!loading && health && (
                <div>

                  <div className="flex items-center gap-3">

                    <div className="w-3 h-3 rounded-full bg-green-400" />

                    <span className="text-green-400 font-semibold">
                      {health.status.toUpperCase()}
                    </span>

                  </div>

                  <p className="text-slate-400 mt-3">
                    Service:{" "}
                    <span className="text-white">
                      {health.service}
                    </span>
                  </p>

                  <p className="text-slate-500 text-sm mt-3">
                    Netty running on port 8080
                  </p>

                </div>
              )}

            </div>

          </section>

          {/* Quiz list */}

          <section className="rounded-2xl border border-slate-800 bg-slate-900 p-6">

            <h2 className="text-xl font-bold">
              Available Quizzes
            </h2>

            <div className="mt-6 space-y-3">

              {quizzes.length === 0 && !loading && (
                <p className="text-slate-500">
                  No quizzes
                </p>
              )}

              {quizzes.map((quiz) => (

                <div
                  key={quiz.id}
                  className="rounded-xl border border-slate-700 bg-slate-800 p-4"
                >

                  <p className="font-semibold">
                    {quiz.title}
                  </p>

                  <p className="text-sm text-slate-500 mt-1">
                    {quiz.id}
                  </p>

                </div>

              ))}

            </div>

          </section>

        </div>

        <button
          onClick={loadData}
          className="mt-6 rounded-xl bg-blue-600 px-5 py-3 font-semibold hover:bg-blue-500"
        >
          Reload from Netty
        </button>

      </div>

    </main>
  );
}