package no.nr.einar.naming.rulebook;

abstract class ParameterRequirement {
	abstract boolean accepts(final MethodIdea methodIdea);
	/*
	static ParameterRequirement derive(final String paramTypeStringOrNull) {
		if (paramTypeStringOrNull == null) {
			return EVERYTHING;
		} else if (paramTypeStringOrNull.equals("")) {
			return NO_PARAMETER;
		} else {
			return new TypeParameterRequirement(paramTypeStringOrNull);
		}
	}
	
	private static ParameterRequirement 
    	EVERYTHING = new ParameterRequirement() {
			@Override
			public boolean accepts(final MethodIdea methodIdea) {
				return true;
			}
			
			@Override
			public String toString() {
				return "ParameterRequirement.EVERYTHING";
			}
		},
	
		NO_PARAMETER = new ParameterRequirement() {
			@Override
			public boolean accepts(final MethodIdea methodIdea) {
				return !methodIdea.takesParameter();
			}
			
			@Override
			public String toString() {
				return "ParameterRequirement.NO_PARAMETER";
			}
		};
		
	private static class TypeParameterRequirement extends ParameterRequirement {
		private final Type requiredParamType;
		
		TypeParameterRequirement(final String paramTypeString) {
			this.requiredParamType = PhraseBuilder.deriveType(paramTypeString);
		}

		@Override
		boolean accepts(final MethodIdea methodIdea) {
			return methodIdea.takesParameter() 
			&& (requiredParamType == Type.ANY || requiredParamType == methodIdea.getParamType());
		}
		
		@Override
		public String toString() {
			return "ParameterRequirement.TypeParameterRequirement[" + requiredParamType + "]";
		}
		
		@Override
		public int hashCode() {
			return 42;
		}
		
		@Override
		public boolean equals(final Object other) {
			return other != null 
			&& this.getClass() == other.getClass() 
			&& this.requiredParamType == 
			       ((ParameterRequirement.TypeParameterRequirement) other).requiredParamType;
		}
	}*/
}