import { api } from './api';
import { DisasterType, LearningModule, PreparednessScore, Quiz, QuizResult, Simulation, SimulationResult } from '../types';

export const educationService = {
  async getAllDisasters(): Promise<DisasterType[]> {
    const res = await api.get('/disasters');
    return res.data.data;
  },

  async getDisasterById(id: number): Promise<DisasterType> {
    const res = await api.get(`/disasters/${id}`);
    return res.data.data;
  },

  async getModules(): Promise<LearningModule[]> {
    const res = await api.get('/modules');
    return res.data.data;
  },

  async getModuleById(id: number): Promise<LearningModule> {
    const res = await api.get(`/modules/${id}`);
    return res.data.data;
  },

  async updateModuleProgress(moduleId: number, percentage = 100, completed = true): Promise<any> {
    const res = await api.post(`/modules/${moduleId}/progress?percentage=${percentage}&completed=${completed}`);
    return res.data.data;
  },

  async getQuizzes(): Promise<Quiz[]> {
    const res = await api.get('/quizzes');
    return res.data.data;
  },

  async getQuizById(id: number): Promise<Quiz> {
    const res = await api.get(`/quizzes/${id}`);
    return res.data.data;
  },

  async submitQuiz(quizId: number, answers: Record<number, number>): Promise<QuizResult> {
    const res = await api.post(`/quizzes/${quizId}/submit`, { answers });
    return res.data.data;
  },

  async getMyQuizResults(): Promise<QuizResult[]> {
    const res = await api.get('/quizzes/results/my');
    return res.data.data;
  },

  async getSimulations(): Promise<Simulation[]> {
    const res = await api.get('/simulations');
    return res.data.data;
  },

  async getSimulationById(id: number): Promise<Simulation> {
    const res = await api.get(`/simulations/${id}`);
    return res.data.data;
  },

  async submitSimulation(simulationId: number, selectedOptionId: number): Promise<SimulationResult> {
    const res = await api.post(`/simulations/${simulationId}/submit`, { selectedOptionId });
    return res.data.data;
  },

  async getMySimulationResults(): Promise<SimulationResult[]> {
    const res = await api.get('/simulations/results/my');
    return res.data.data;
  },

  async getPreparednessScore(): Promise<PreparednessScore> {
    const res = await api.get('/preparedness/score');
    return res.data.data;
  },
};
