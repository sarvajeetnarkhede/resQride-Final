import React, { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useNavigate } from 'react-router-dom';
import { useAuthStore } from '../store/authStore';
import { Button } from '../components/ui/button';
import { Loader2, ArrowLeft, Car, Wrench, Battery, Fuel, Lock, AlertTriangle } from 'lucide-react';
import { toast } from 'react-toastify';
import apiClient from '../api/client';
import { LocationPicker } from '../components/LocationPicker';

const serviceRequestSchema = z.object({
  location: z.string().min(5, 'Please enter a valid location'),
  problemType: z.string().min(1, 'Please select a problem type'),
  latitude: z.number().optional(),
  longitude: z.number().optional(),
});

const SERVICE_PRICES: Record<string, number> = {
  'TOWING': 1500.00,
  'TIRE_CHANGE': 800.00,
  'BATTERY': 3000.00,
  'FUEL': 600.00,
  'LOCKOUT': 300.00,
  'MECHANIC': 1500.00
};

const SERVICE_TYPES = [
  { id: 'TOWING', name: 'Towing Service', price: 1500, icon: Car, desc: 'Vehicle transport to nearest garage' },
  { id: 'TIRE_CHANGE', name: 'Flat Tire', price: 800, icon: AlertTriangle, desc: 'Puncture repair or spare tire change' },
  { id: 'BATTERY', name: 'Battery Jump', price: 3000, icon: Battery, desc: 'Jump start or battery replacement' },
  { id: 'FUEL', name: 'Fuel Delivery', price: 600, icon: Fuel, desc: 'Emergency fuel (Petrol/Diesel)' },
  { id: 'LOCKOUT', name: 'Lockout', price: 300, icon: Lock, desc: 'Key retrieval or door unlocking' },
  { id: 'MECHANIC', name: 'General Mechanic', price: 1500, icon: Wrench, desc: 'Engine issues, breakdown, etc.' },
];

export const ServiceRequest: React.FC = () => {
  const { setValue } = useForm({
    resolver: zodResolver(serviceRequestSchema),
  });

  const [step, setStep] = useState<1 | 2>(1);
  const [selectedProblem, setSelectedProblem] = useState<string>('');
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();
  const { isAuthenticated } = useAuthStore();

  useEffect(() => {
    if (!isAuthenticated) {
      toast.info('Please login to request assistance');
      navigate('/login');
    }
  }, [isAuthenticated, navigate]);

  const handleProblemSelect = (id: string) => {
    setSelectedProblem(id);
    setValue('problemType', id);
    setStep(2); // Move to location step
  };

  const handleLocationConfirm = async (location: string, lat: number, lng: number) => {
    setValue('location', location);
    setValue('latitude', lat);
    setValue('longitude', lng);

    // Submit immediately after location confirmation
    await onSubmit({
      problemType: selectedProblem,
      location,
      latitude: lat,
      longitude: lng
    });
  };

  const onSubmit = async (data: any) => {
    setIsLoading(true);
    try {
      const amount = SERVICE_PRICES[data.problemType] || 0;

      await apiClient.post('/requests', {
        location: data.location,
        latitude: data.latitude,
        longitude: data.longitude,
        problemType: data.problemType,
        amount: amount,
      });

      toast.success('Service request submitted successfully! Searching for mechanics...');
      navigate('/dashboard');
    } catch (error) {
      console.error('Submission failed:', error);
      toast.error('Failed to submit request. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-[calc(100vh-4rem)] bg-gray-50 py-8">
      <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8">

        {/* Step Indicator */}
        <div className="mb-8 flex items-center justify-between">
          <div className="flex items-center gap-2">
            {step === 2 && (
              <Button variant="ghost" size="sm" onClick={() => setStep(1)} className="mr-2">
                <ArrowLeft className="h-4 w-4" />
              </Button>
            )}
            <div>
              <h1 className="text-2xl font-bold text-gray-900">
                {step === 1 ? 'Select Service' : 'Confirm Location'}
              </h1>
              <p className="text-sm text-gray-500">
                Step {step} of 2
              </p>
            </div>
          </div>
          <div className="flex gap-2">
            <div className={`h-2 w-12 rounded-full ${step >= 1 ? 'bg-blue-600' : 'bg-gray-200'}`} />
            <div className={`h-2 w-12 rounded-full ${step >= 2 ? 'bg-blue-600' : 'bg-gray-200'}`} />
          </div>
        </div>

        {step === 1 && (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4 animate-in fade-in slide-in-from-bottom-4 duration-500">
            {SERVICE_TYPES.map((service) => (
              <div
                key={service.id}
                onClick={() => handleProblemSelect(service.id)}
                className="bg-white p-6 rounded-xl border border-gray-200 shadow-sm hover:shadow-md hover:border-blue-500 cursor-pointer transition-all group"
              >
                <div className="flex items-start justify-between mb-4">
                  <div className="p-3 bg-blue-50 rounded-lg group-hover:bg-blue-100 transition-colors">
                    <service.icon className="h-6 w-6 text-blue-600" />
                  </div>
                  <span className="font-semibold text-gray-900">₹{service.price}</span>
                </div>
                <h3 className="font-semibold text-gray-900 mb-1">{service.name}</h3>
                <p className="text-sm text-gray-500">{service.desc}</p>
              </div>
            ))}
          </div>
        )}

        {step === 2 && (
          <div className="bg-white rounded-xl shadow-lg border border-gray-200 overflow-hidden animate-in fade-in zoom-in duration-300">
            {isLoading ? (
              <div className="h-[600px] flex flex-col items-center justify-center">
                <Loader2 className="h-12 w-12 text-blue-600 animate-spin mb-4" />
                <p className="text-lg font-medium text-gray-700">Submitting Request...</p>
              </div>
            ) : (
              <LocationPicker
                onConfirm={handleLocationConfirm}
              />
            )}
          </div>
        )}

      </div>
    </div>
  );
};
