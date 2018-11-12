	import './support/gentypes';
	import { Proxy } from './support/proxy';


	export class BDDNoTerminalNode  {

		___nsuri: string = "http://specmate.com/20180925/model/bdd";
		public url: string;
		public className: string = "BDDNoTerminalNode";
		public static className: string = "BDDNoTerminalNode";

		// Attributes
		public id: EString;
		public name: EString;
		public description: EString;
		public x: EDouble;
		public y: EDouble;
		public variable: EString;
		public condition: EString;

		// References
		
		public tracesTo: Proxy[];
		public tracesFrom: Proxy[];
		public outgoingConnections: Proxy[];
		public incomingConnections: Proxy[];

		// Containment


	}

